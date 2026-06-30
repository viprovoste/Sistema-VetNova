package vetnova.ventas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vetnova.ventas.client.InventarioClient;
import vetnova.ventas.client.dto.MovimientoRequestDTO;
import vetnova.ventas.client.dto.ProductoDTO;
import vetnova.ventas.dto.ItemVentaRequest;
import vetnova.ventas.dto.PagarVentaRequest;
import vetnova.ventas.dto.VentaRequest;
import vetnova.ventas.dto.VentaResponse;
import vetnova.ventas.exception.ResourceNotFoundException;
import vetnova.ventas.exception.VentaYaProcesadaException;
import vetnova.ventas.model.*;
import vetnova.ventas.repository.CarritoCompraRepository;
import vetnova.ventas.repository.DetalleVentaRepository;
import vetnova.ventas.repository.ItemCarritoRepository;
import vetnova.ventas.repository.VentaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VentaService - pruebas unitarias")
class VentaServiceTest {

    private static final Long SUCURSAL_WEB_ID = 1L;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private CarritoCompraRepository carritoRepository;

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private InventarioClient inventarioClient;

    private VentaServiceImpl ventaService;

    private Venta ventaPendienteSucursal;

    @BeforeEach
    void setUp() {
        ventaService = new VentaServiceImpl(ventaRepository, detalleVentaRepository, carritoRepository,
                itemCarritoRepository, inventarioClient, SUCURSAL_WEB_ID);

        ventaPendienteSucursal = Venta.builder()
                .id(100L)
                .clienteId(50L)
                .sucursalId(2L)
                .usuarioId(10L)
                .tipoVenta(TipoVenta.SUCURSAL)
                .estado(EstadoVenta.PENDIENTE)
                .subtotal(new BigDecimal("20000"))
                .descuento(BigDecimal.ZERO)
                .total(new BigDecimal("20000"))
                .build();
    }

    @Nested
    @DisplayName("Creación de venta directa (caja)")
    class VentaDirecta {

        @Test
        @DisplayName("Calcula el subtotal y total consultando precios vigentes en Inventario")
        void creaVentaDirectaCorrectamente() {
            ProductoDTO producto = new ProductoDTO(7L, "Shampoo antipulgas", new BigDecimal("9000"), true);
            VentaRequest request = new VentaRequest(50L, 2L, 10L, MetodoPago.EFECTIVO, null, null,
                    List.of(new ItemVentaRequest(7L, 2)));

            when(inventarioClient.obtenerProducto(7L)).thenReturn(producto);
            when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> {
                Venta v = inv.getArgument(0);
                v.setId(200L);
                return v;
            });
            when(detalleVentaRepository.findByVentaId(200L)).thenReturn(List.of());

            VentaResponse response = ventaService.crearVentaDirecta(request);

            assertThat(response.getSubtotal()).isEqualByComparingTo("18000");
            assertThat(response.getTotal()).isEqualByComparingTo("18000");
            assertThat(response.getEstado()).isEqualTo(EstadoVenta.PENDIENTE);
        }

        @Test
        @DisplayName("Aplica el descuento indicado sobre el subtotal")
        void aplicaDescuento() {
            ProductoDTO producto = new ProductoDTO(7L, "Shampoo antipulgas", new BigDecimal("10000"), true);
            VentaRequest request = new VentaRequest(50L, 2L, 10L, MetodoPago.DEBITO, new BigDecimal("1000"), null,
                    List.of(new ItemVentaRequest(7L, 1)));

            when(inventarioClient.obtenerProducto(7L)).thenReturn(producto);
            when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));
            when(detalleVentaRepository.findByVentaId(any())).thenReturn(List.of());

            VentaResponse response = ventaService.crearVentaDirecta(request);

            assertThat(response.getTotal()).isEqualByComparingTo("9000");
        }
    }

    @Nested
    @DisplayName("Confirmación de carrito web")
    class ConfirmarCarrito {

        @Test
        @DisplayName("Convierte un carrito activo con items en una venta PENDIENTE")
        void confirmaCarritoActivoConItems() {
            CarritoCompra carrito = CarritoCompra.builder().id(5L).clienteId(50L).estado(EstadoCarrito.ACTIVO).build();
            ItemCarrito item = ItemCarrito.builder().id(1L).carritoId(5L).productoId(7L)
                    .nombreProducto("Vacuna Triple Felina").precioUnitario(new BigDecimal("15000")).cantidad(1).build();

            when(carritoRepository.findById(5L)).thenReturn(Optional.of(carrito));
            when(itemCarritoRepository.findByCarritoId(5L)).thenReturn(List.of(item));
            when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> {
                Venta v = inv.getArgument(0);
                v.setId(300L);
                return v;
            });
            when(detalleVentaRepository.findByVentaId(300L)).thenReturn(List.of());

            VentaResponse response = ventaService.confirmarCarrito(5L);

            assertThat(response.getTipoVenta()).isEqualTo(TipoVenta.WEB);
            assertThat(response.getTotal()).isEqualByComparingTo("15000");
            assertThat(carrito.getEstado()).isEqualTo(EstadoCarrito.COMPLETADO);
            verify(carritoRepository).save(carrito);
        }

        @Test
        @DisplayName("Rechaza confirmar un carrito vacío")
        void rechazaCarritoVacio() {
            CarritoCompra carrito = CarritoCompra.builder().id(5L).clienteId(50L).estado(EstadoCarrito.ACTIVO).build();
            when(carritoRepository.findById(5L)).thenReturn(Optional.of(carrito));
            when(itemCarritoRepository.findByCarritoId(5L)).thenReturn(List.of());

            assertThatThrownBy(() -> ventaService.confirmarCarrito(5L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("vacío");

            verify(ventaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Rechaza confirmar un carrito que ya no está activo")
        void rechazaCarritoNoActivo() {
            CarritoCompra carritoCompletado = CarritoCompra.builder().id(5L).estado(EstadoCarrito.COMPLETADO).build();
            when(carritoRepository.findById(5L)).thenReturn(Optional.of(carritoCompletado));

            assertThatThrownBy(() -> ventaService.confirmarCarrito(5L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Pago de una venta")
    class PagoVenta {

        @Test
        @DisplayName("Registra una SALIDA en Inventario por cada línea de la venta y marca la venta como PAGADA")
        void pagaVentaCorrectamente() {
            DetalleVenta detalle = DetalleVenta.builder()
                    .id(1L).ventaId(100L).productoId(7L).nombreProducto("Shampoo antipulgas")
                    .cantidad(2).precioUnitario(new BigDecimal("9000")).subtotalLinea(new BigDecimal("18000"))
                    .build();

            when(ventaRepository.findById(100L)).thenReturn(Optional.of(ventaPendienteSucursal));
            when(detalleVentaRepository.findByVentaId(100L)).thenReturn(List.of(detalle));
            when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

            VentaResponse response = ventaService.pagar(100L, new PagarVentaRequest(MetodoPago.EFECTIVO));

            assertThat(response.getEstado()).isEqualTo(EstadoVenta.PAGADA);
            assertThat(response.getMetodoPago()).isEqualTo(MetodoPago.EFECTIVO);

            ArgumentCaptor<MovimientoRequestDTO> captor = ArgumentCaptor.forClass(MovimientoRequestDTO.class);
            verify(inventarioClient).registrarSalida(captor.capture());
            assertThat(captor.getValue().getTipoMovimiento()).isEqualTo("SALIDA");
            assertThat(captor.getValue().getSucursalId()).isEqualTo(2L); // sucursal de la venta, no la web
            assertThat(captor.getValue().getCantidad()).isEqualTo(2);
        }

        @Test
        @DisplayName("Usa la sucursal web como referencia de descuento cuando la venta no tiene sucursal (compra web)")
        void usaSucursalWebSiVentaEsOnline() {
            Venta ventaWeb = Venta.builder().id(101L).clienteId(50L).sucursalId(null)
                    .tipoVenta(TipoVenta.WEB).estado(EstadoVenta.PENDIENTE)
                    .subtotal(new BigDecimal("5000")).descuento(BigDecimal.ZERO).total(new BigDecimal("5000")).build();
            DetalleVenta detalle = DetalleVenta.builder().id(2L).ventaId(101L).productoId(9L)
                    .nombreProducto("Correa para perro").cantidad(1)
                    .precioUnitario(new BigDecimal("5000")).subtotalLinea(new BigDecimal("5000")).build();

            when(ventaRepository.findById(101L)).thenReturn(Optional.of(ventaWeb));
            when(detalleVentaRepository.findByVentaId(101L)).thenReturn(List.of(detalle));
            when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

            ventaService.pagar(101L, new PagarVentaRequest(MetodoPago.TRANSFERENCIA));

            ArgumentCaptor<MovimientoRequestDTO> captor = ArgumentCaptor.forClass(MovimientoRequestDTO.class);
            verify(inventarioClient).registrarSalida(captor.capture());
            assertThat(captor.getValue().getSucursalId()).isEqualTo(SUCURSAL_WEB_ID);
        }

        @Test
        @DisplayName("Rechaza pagar una venta que ya no está PENDIENTE")
        void rechazaPagarVentaYaProcesada() {
            ventaPendienteSucursal.setEstado(EstadoVenta.PAGADA);
            when(ventaRepository.findById(100L)).thenReturn(Optional.of(ventaPendienteSucursal));

            assertThatThrownBy(() -> ventaService.pagar(100L, new PagarVentaRequest(MetodoPago.EFECTIVO)))
                    .isInstanceOf(VentaYaProcesadaException.class);

            verify(inventarioClient, never()).registrarSalida(any());
        }

        @Test
        @DisplayName("Lanza excepción si la venta no existe")
        void lanzaExcepcionSiVentaNoExiste() {
            when(ventaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ventaService.pagar(999L, new PagarVentaRequest(MetodoPago.EFECTIVO)))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Cancelación de una venta")
    class CancelacionVenta {

        @Test
        @DisplayName("Cancela una venta pendiente")
        void cancelaVentaPendiente() {
            when(ventaRepository.findById(100L)).thenReturn(Optional.of(ventaPendienteSucursal));
            when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));
            when(detalleVentaRepository.findByVentaId(100L)).thenReturn(List.of());

            VentaResponse response = ventaService.cancelar(100L);

            assertThat(response.getEstado()).isEqualTo(EstadoVenta.CANCELADA);
        }

        @Test
        @DisplayName("Rechaza cancelar una venta que ya fue pagada")
        void rechazaCancelarVentaPagada() {
            ventaPendienteSucursal.setEstado(EstadoVenta.PAGADA);
            when(ventaRepository.findById(100L)).thenReturn(Optional.of(ventaPendienteSucursal));

            assertThatThrownBy(() -> ventaService.cancelar(100L))
                    .isInstanceOf(VentaYaProcesadaException.class);

            verify(ventaRepository, never()).save(any());
        }
    }
}
