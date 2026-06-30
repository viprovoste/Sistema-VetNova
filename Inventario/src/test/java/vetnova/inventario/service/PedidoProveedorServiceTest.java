package vetnova.inventario.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vetnova.inventario.dto.*;
import vetnova.inventario.exception.ResourceNotFoundException;
import vetnova.inventario.model.*;
import vetnova.inventario.repository.DetallePedidoProveedorRepository;
import vetnova.inventario.repository.MovimientoInventarioRepository;
import vetnova.inventario.repository.PedidoProveedorRepository;
import vetnova.inventario.repository.StockRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoProveedorService - pruebas unitarias")
class PedidoProveedorServiceTest {

    @Mock
    private PedidoProveedorRepository pedidoProveedorRepository;
    @Mock
    private DetallePedidoProveedorRepository detalleRepository;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @InjectMocks
    private PedidoProveedorServiceImpl pedidoProveedorService;

    private PedidoProveedor pedidoPendiente;

    @BeforeEach
    void setUp() {
        pedidoPendiente = PedidoProveedor.builder()
                .id(1L).proveedorId(10L).sucursalId(2L).usuarioId(5L)
                .estado(EstadoPedido.PENDIENTE)
                .build();
    }

    @Nested
    @DisplayName("Creación de pedido")
    class Crear {

        @Test
        @DisplayName("Crea un pedido con sus detalles")
        void creaPedidoConDetalles() {
            DetallePedidoRequest detalleReq = new DetallePedidoRequest(7L, 20, new BigDecimal("1500"));
            PedidoProveedorRequest request = new PedidoProveedorRequest(10L, 2L, 5L, "Reposición urgente",
                    List.of(detalleReq));

            when(pedidoProveedorRepository.save(any(PedidoProveedor.class))).thenAnswer(inv -> {
                PedidoProveedor p = inv.getArgument(0);
                p.setId(1L);
                return p;
            });
            when(detalleRepository.findByPedidoId(1L)).thenReturn(List.of());

            PedidoProveedorResponse response = pedidoProveedorService.crear(request);

            assertThat(response.getEstado()).isEqualTo(EstadoPedido.PENDIENTE);
            assertThat(response.getProveedorId()).isEqualTo(10L);
            verify(detalleRepository).save(any(DetallePedidoProveedor.class));
        }
    }

    @Nested
    @DisplayName("Actualización de estado")
    class ActualizarEstado {

        @Test
        @DisplayName("Cambia el estado de un pedido pendiente")
        void cambiaEstado() {
            when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedidoPendiente));
            when(pedidoProveedorRepository.save(any(PedidoProveedor.class))).thenAnswer(inv -> inv.getArgument(0));
            when(detalleRepository.findByPedidoId(1L)).thenReturn(List.of());

            PedidoProveedorResponse response = pedidoProveedorService.actualizarEstado(
                    1L, new ActualizarEstadoPedidoRequest(EstadoPedido.ENVIADO));

            assertThat(response.getEstado()).isEqualTo(EstadoPedido.ENVIADO);
        }

        @Test
        @DisplayName("Rechaza cambiar el estado de un pedido ya recibido")
        void rechazaCambioSiYaRecibido() {
            pedidoPendiente.setEstado(EstadoPedido.RECIBIDO);
            when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedidoPendiente));

            assertThatThrownBy(() -> pedidoProveedorService.actualizarEstado(
                    1L, new ActualizarEstadoPedidoRequest(EstadoPedido.CANCELADO)))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(pedidoProveedorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Recepción de mercadería")
    class Recibir {

        @Test
        @DisplayName("Marca el pedido como RECIBIDO, actualiza stock y registra movimiento ENTRADA")
        void recibePedidoCorrectamente() {
            DetallePedidoProveedor detalle = DetallePedidoProveedor.builder()
                    .id(50L).pedidoId(1L).productoId(7L).cantidadSolicitada(20).cantidadRecibida(0).build();
            RecepcionDetalleItem item = new RecepcionDetalleItem(50L, 18);
            RecibirPedidoRequest request = new RecibirPedidoRequest(List.of(item));

            when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedidoPendiente));
            when(detalleRepository.findById(50L)).thenReturn(Optional.of(detalle));
            when(stockRepository.findByProductoIdAndSucursalId(7L, 2L)).thenReturn(Optional.empty());
            when(pedidoProveedorRepository.save(any(PedidoProveedor.class))).thenAnswer(inv -> inv.getArgument(0));
            when(detalleRepository.findByPedidoId(1L)).thenReturn(List.of(detalle));

            PedidoProveedorResponse response = pedidoProveedorService.recibir(1L, request);

            assertThat(response.getEstado()).isEqualTo(EstadoPedido.RECIBIDO);
            assertThat(detalle.getCantidadRecibida()).isEqualTo(18);

            ArgumentCaptor<Stock> stockCaptor = ArgumentCaptor.forClass(Stock.class);
            verify(stockRepository).save(stockCaptor.capture());
            assertThat(stockCaptor.getValue().getCantidadDisponible()).isEqualTo(18);

            ArgumentCaptor<MovimientoInventario> movCaptor = ArgumentCaptor.forClass(MovimientoInventario.class);
            verify(movimientoRepository).save(movCaptor.capture());
            assertThat(movCaptor.getValue().getTipoMovimiento()).isEqualTo(TipoMovimiento.ENTRADA);
            assertThat(movCaptor.getValue().getCantidad()).isEqualTo(18);
        }

        @Test
        @DisplayName("Rechaza recibir un pedido ya recibido")
        void rechazaRecepcionDuplicada() {
            pedidoPendiente.setEstado(EstadoPedido.RECIBIDO);
            when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedidoPendiente));

            assertThatThrownBy(() -> pedidoProveedorService.recibir(1L, new RecibirPedidoRequest(List.of())))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(detalleRepository, never()).save(any());
        }

        @Test
        @DisplayName("Lanza excepción si el detalle indicado no existe")
        void lanzaExcepcionSiDetalleNoExiste() {
            RecepcionDetalleItem item = new RecepcionDetalleItem(999L, 5);
            when(pedidoProveedorRepository.findById(1L)).thenReturn(Optional.of(pedidoPendiente));
            when(detalleRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pedidoProveedorService.recibir(1L, new RecibirPedidoRequest(List.of(item))))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Consultas")
    class Consultas {

        @Test
        @DisplayName("Lista todos los pedidos")
        void listaTodos() {
            when(pedidoProveedorRepository.findAll()).thenReturn(List.of(pedidoPendiente));
            when(detalleRepository.findByPedidoId(1L)).thenReturn(List.of());

            List<PedidoProveedorResponse> resultado = pedidoProveedorService.listarTodos();

            assertThat(resultado).hasSize(1);
        }

        @Test
        @DisplayName("Lanza excepción si el pedido no existe")
        void lanzaExcepcionSiPedidoNoExiste() {
            when(pedidoProveedorRepository.findById(404L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pedidoProveedorService.obtenerPorId(404L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
