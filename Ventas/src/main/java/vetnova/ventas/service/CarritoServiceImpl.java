package vetnova.ventas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vetnova.ventas.client.InventarioClient;
import vetnova.ventas.client.dto.ItemVerificacionStockDTO;
import vetnova.ventas.client.dto.ProductoDTO;
import vetnova.ventas.client.dto.VerificarStockRequestDTO;
import vetnova.ventas.client.dto.VerificarStockResponseDTO;
import vetnova.ventas.dto.ActualizarCantidadRequest;
import vetnova.ventas.dto.CarritoResponse;
import vetnova.ventas.dto.ItemCarritoRequest;
import vetnova.ventas.dto.ItemCarritoResponse;
import vetnova.ventas.exception.ResourceNotFoundException;
import vetnova.ventas.exception.StockNoDisponibleException;
import vetnova.ventas.model.CarritoCompra;
import vetnova.ventas.model.EstadoCarrito;
import vetnova.ventas.model.ItemCarrito;
import vetnova.ventas.repository.CarritoCompraRepository;
import vetnova.ventas.repository.ItemCarritoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoServiceImpl implements CarritoService {

    private final CarritoCompraRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final InventarioClient inventarioClient;

    /** Sucursal/bodega central usada para verificar y descontar stock de las compras web. */
    private final Long sucursalWebId;

    public CarritoServiceImpl(CarritoCompraRepository carritoRepository,
                               ItemCarritoRepository itemCarritoRepository,
                               InventarioClient inventarioClient,
                               @Value("${vetnova.inventario.sucursal-web-id}") Long sucursalWebId) {
        this.carritoRepository = carritoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.inventarioClient = inventarioClient;
        this.sucursalWebId = sucursalWebId;
    }

    @Override
    public CarritoResponse obtenerCarritoActivo(Long clienteId) {
        return mapearCarrito(obtenerOcrearCarritoActivo(clienteId));
    }

    /**
     * Agrega un producto al carrito. Antes de hacerlo: (1) consulta a Inventario el nombre y
     * precio vigente del producto (snapshot), y (2) verifica que exista stock suficiente para
     * la cantidad total deseada (lo ya en el carrito + lo nuevo).
     */
    @Override
    @Transactional
    public CarritoResponse agregarItem(Long clienteId, ItemCarritoRequest request) {
        CarritoCompra carrito = obtenerOcrearCarritoActivo(clienteId);

        ProductoDTO producto = inventarioClient.obtenerProducto(request.getProductoId());
        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new ResourceNotFoundException(
                    "El producto '" + producto.getNombre() + "' ya no está disponible para la venta");
        }

        Optional<ItemCarrito> itemExistente =
                itemCarritoRepository.findByCarritoIdAndProductoId(carrito.getId(), request.getProductoId());

        int cantidadTotalDeseada = request.getCantidad() + itemExistente.map(ItemCarrito::getCantidad).orElse(0);

        VerificarStockResponseDTO verificacion = inventarioClient.verificarDisponibilidad(
                new VerificarStockRequestDTO(sucursalWebId,
                        List.of(new ItemVerificacionStockDTO(request.getProductoId(), cantidadTotalDeseada))));

        if (!verificacion.isDisponible()) {
            throw new StockNoDisponibleException(
                    "No hay stock suficiente para: " + String.join(", ", verificacion.getProductosSinStock()));
        }

        if (itemExistente.isPresent()) {
            ItemCarrito item = itemExistente.get();
            item.setCantidad(cantidadTotalDeseada);
            itemCarritoRepository.save(item);
        } else {
            itemCarritoRepository.save(ItemCarrito.builder()
                    .carritoId(carrito.getId())
                    .productoId(producto.getId())
                    .nombreProducto(producto.getNombre())
                    .precioUnitario(producto.getPrecio())
                    .cantidad(request.getCantidad())
                    .build());
        }

        return mapearCarrito(carrito);
    }

    @Override
    @Transactional
    public CarritoResponse actualizarCantidad(Long carritoId, Long itemId, ActualizarCantidadRequest request) {
        CarritoCompra carrito = buscarCarrito(carritoId);
        ItemCarrito item = buscarItem(carritoId, itemId);

        VerificarStockResponseDTO verificacion = inventarioClient.verificarDisponibilidad(
                new VerificarStockRequestDTO(sucursalWebId,
                        List.of(new ItemVerificacionStockDTO(item.getProductoId(), request.getCantidad()))));

        if (!verificacion.isDisponible()) {
            throw new StockNoDisponibleException(
                    "No hay stock suficiente para: " + String.join(", ", verificacion.getProductosSinStock()));
        }

        item.setCantidad(request.getCantidad());
        itemCarritoRepository.save(item);
        return mapearCarrito(carrito);
    }

    @Override
    @Transactional
    public CarritoResponse eliminarItem(Long carritoId, Long itemId) {
        CarritoCompra carrito = buscarCarrito(carritoId);
        ItemCarrito item = buscarItem(carritoId, itemId);
        itemCarritoRepository.delete(item);
        return mapearCarrito(carrito);
    }

    @Override
    @Transactional
    public CarritoResponse vaciarCarrito(Long carritoId) {
        CarritoCompra carrito = buscarCarrito(carritoId);
        itemCarritoRepository.deleteByCarritoId(carritoId);
        return mapearCarrito(carrito);
    }

    private CarritoCompra obtenerOcrearCarritoActivo(Long clienteId) {
        return carritoRepository.findByClienteIdAndEstado(clienteId, EstadoCarrito.ACTIVO)
                .orElseGet(() -> carritoRepository.save(
                        CarritoCompra.builder().clienteId(clienteId).estado(EstadoCarrito.ACTIVO).build()));
    }

    private CarritoCompra buscarCarrito(Long id) {
        return carritoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el carrito con id: " + id));
    }

    private ItemCarrito buscarItem(Long carritoId, Long itemId) {
        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el item con id: " + itemId));
        if (!item.getCarritoId().equals(carritoId)) {
            throw new ResourceNotFoundException("El item " + itemId + " no pertenece al carrito " + carritoId);
        }
        return item;
    }

    private CarritoResponse mapearCarrito(CarritoCompra carrito) {
        List<ItemCarritoResponse> items = itemCarritoRepository.findByCarritoId(carrito.getId()).stream()
                .map(item -> ItemCarritoResponse.builder()
                        .id(item.getId())
                        .productoId(item.getProductoId())
                        .nombreProducto(item.getNombreProducto())
                        .precioUnitario(item.getPrecioUnitario())
                        .cantidad(item.getCantidad())
                        .subtotal(item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                        .build())
                .toList();

        BigDecimal total = items.stream()
                .map(ItemCarritoResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CarritoResponse.builder()
                .id(carrito.getId())
                .clienteId(carrito.getClienteId())
                .estado(carrito.getEstado())
                .items(items)
                .total(total)
                .build();
    }
}
