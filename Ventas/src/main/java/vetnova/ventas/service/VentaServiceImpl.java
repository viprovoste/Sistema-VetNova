package vetnova.ventas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vetnova.ventas.client.InventarioClient;
import vetnova.ventas.client.dto.MovimientoRequestDTO;
import vetnova.ventas.client.dto.ProductoDTO;
import vetnova.ventas.dto.*;
import vetnova.ventas.exception.ResourceNotFoundException;
import vetnova.ventas.exception.VentaYaProcesadaException;
import vetnova.ventas.model.*;
import vetnova.ventas.repository.CarritoCompraRepository;
import vetnova.ventas.repository.DetalleVentaRepository;
import vetnova.ventas.repository.ItemCarritoRepository;
import vetnova.ventas.repository.VentaRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final CarritoCompraRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final InventarioClient inventarioClient;
    private final Long sucursalWebId;

    public VentaServiceImpl(VentaRepository ventaRepository,
                             DetalleVentaRepository detalleVentaRepository,
                             CarritoCompraRepository carritoRepository,
                             ItemCarritoRepository itemCarritoRepository,
                             InventarioClient inventarioClient,
                             @Value("${vetnova.inventario.sucursal-web-id}") Long sucursalWebId) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.carritoRepository = carritoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.inventarioClient = inventarioClient;
        this.sucursalWebId = sucursalWebId;
    }

    @Override
    public List<VentaResponse> listarTodas() {
        return ventaRepository.findAll().stream().map(this::mapearVenta).toList();
    }

    @Override
    public VentaResponse obtenerPorId(Long id) {
        return mapearVenta(buscarVenta(id));
    }

    @Override
    public List<VentaResponse> listarPorCliente(Long clienteId) {
        return ventaRepository.findByClienteId(clienteId).stream().map(this::mapearVenta).toList();
    }

    @Override
    public List<VentaResponse> listarPorSucursal(Long sucursalId) {
        return ventaRepository.findBySucursalId(sucursalId).stream().map(this::mapearVenta).toList();
    }

    /** Venta directa en caja (sucursal). Consulta a Inventario el precio vigente de cada producto. */
    @Override
    @Transactional
    public VentaResponse crearVentaDirecta(VentaRequest request) {
        List<DetalleVenta> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemVentaRequest itemReq : request.getItems()) {
            ProductoDTO producto = inventarioClient.obtenerProducto(itemReq.getProductoId());
            BigDecimal subtotalLinea = producto.getPrecio().multiply(BigDecimal.valueOf(itemReq.getCantidad()));
            subtotal = subtotal.add(subtotalLinea);

            detalles.add(DetalleVenta.builder()
                    .productoId(producto.getId())
                    .nombreProducto(producto.getNombre())
                    .cantidad(itemReq.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .subtotalLinea(subtotalLinea)
                    .build());
        }

        BigDecimal descuento = request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(descuento);

        Venta venta = Venta.builder()
                .clienteId(request.getClienteId())
                .sucursalId(request.getSucursalId())
                .usuarioId(request.getUsuarioId())
                .tipoVenta(TipoVenta.SUCURSAL)
                .estado(EstadoVenta.PENDIENTE)
                .metodoPago(request.getMetodoPago())
                .subtotal(subtotal)
                .descuento(descuento)
                .total(total)
                .observaciones(request.getObservaciones())
                .build();
        venta = ventaRepository.save(venta);

        for (DetalleVenta detalle : detalles) {
            detalle.setVentaId(venta.getId());
            detalleVentaRepository.save(detalle);
        }

        return mapearVenta(venta);
    }

    /** Convierte un carrito web activo en una venta PENDIENTE de pago. */
    @Override
    @Transactional
    public VentaResponse confirmarCarrito(Long carritoId) {
        CarritoCompra carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el carrito con id: " + carritoId));

        if (carrito.getEstado() != EstadoCarrito.ACTIVO) {
            throw new IllegalArgumentException("El carrito no está activo y no puede confirmarse");
        }

        List<ItemCarrito> items = itemCarritoRepository.findByCarritoId(carritoId);
        if (items.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío, no se puede confirmar una venta sin productos");
        }

        BigDecimal subtotal = items.stream()
                .map(i -> i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Venta venta = Venta.builder()
                .clienteId(carrito.getClienteId())
                .carritoId(carrito.getId())
                .tipoVenta(TipoVenta.WEB)
                .estado(EstadoVenta.PENDIENTE)
                .subtotal(subtotal)
                .descuento(BigDecimal.ZERO)
                .total(subtotal)
                .build();
        venta = ventaRepository.save(venta);

        for (ItemCarrito item : items) {
            detalleVentaRepository.save(DetalleVenta.builder()
                    .ventaId(venta.getId())
                    .productoId(item.getProductoId())
                    .nombreProducto(item.getNombreProducto())
                    .cantidad(item.getCantidad())
                    .precioUnitario(item.getPrecioUnitario())
                    .subtotalLinea(item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                    .build());
        }

        carrito.setEstado(EstadoCarrito.COMPLETADO);
        carritoRepository.save(carrito);

        return mapearVenta(venta);
    }

    /**
     * Confirma el pago de una venta PENDIENTE. Por cada línea, llama a Inventario para
     * registrar un movimiento de tipo SALIDA, descontando el stock real de la sucursal
     * (o de la bodega web, si la venta fue por catálogo online).
     */
    @Override
    @Transactional
    public VentaResponse pagar(Long ventaId, PagarVentaRequest request) {
        Venta venta = buscarVenta(ventaId);

        if (venta.getEstado() != EstadoVenta.PENDIENTE) {
            throw new VentaYaProcesadaException(
                    "La venta ya se encuentra en estado " + venta.getEstado() + " y no puede pagarse nuevamente");
        }

        Long sucursalParaDescuento = venta.getSucursalId() != null ? venta.getSucursalId() : sucursalWebId;

        for (DetalleVenta detalle : detalleVentaRepository.findByVentaId(ventaId)) {
            inventarioClient.registrarSalida(new MovimientoRequestDTO(
                    detalle.getProductoId(),
                    sucursalParaDescuento,
                    "SALIDA",
                    detalle.getCantidad(),
                    "Venta #" + venta.getId(),
                    venta.getUsuarioId(),
                    "VENTA-" + venta.getId()));
        }

        venta.setEstado(EstadoVenta.PAGADA);
        venta.setMetodoPago(request.getMetodoPago());
        return mapearVenta(ventaRepository.save(venta));
    }

    @Override
    @Transactional
    public VentaResponse cancelar(Long ventaId) {
        Venta venta = buscarVenta(ventaId);
        if (venta.getEstado() == EstadoVenta.PAGADA) {
            throw new VentaYaProcesadaException("No se puede cancelar una venta que ya fue pagada; use un reembolso");
        }
        venta.setEstado(EstadoVenta.CANCELADA);
        return mapearVenta(ventaRepository.save(venta));
    }

    private Venta buscarVenta(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la venta con id: " + id));
    }

    private VentaResponse mapearVenta(Venta venta) {
        List<DetalleVentaResponse> detalles = detalleVentaRepository.findByVentaId(venta.getId()).stream()
                .map(d -> DetalleVentaResponse.builder()
                        .id(d.getId())
                        .productoId(d.getProductoId())
                        .nombreProducto(d.getNombreProducto())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotalLinea(d.getSubtotalLinea())
                        .build())
                .toList();

        return VentaResponse.builder()
                .id(venta.getId())
                .clienteId(venta.getClienteId())
                .sucursalId(venta.getSucursalId())
                .usuarioId(venta.getUsuarioId())
                .tipoVenta(venta.getTipoVenta())
                .estado(venta.getEstado())
                .metodoPago(venta.getMetodoPago())
                .subtotal(venta.getSubtotal())
                .descuento(venta.getDescuento())
                .total(venta.getTotal())
                .observaciones(venta.getObservaciones())
                .fechaVenta(venta.getFechaVenta())
                .detalles(detalles)
                .build();
    }
}
