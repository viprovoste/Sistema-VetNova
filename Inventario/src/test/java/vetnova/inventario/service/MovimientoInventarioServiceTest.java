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
import vetnova.inventario.client.AuthClient;
import vetnova.inventario.dto.MovimientoRequest;
import vetnova.inventario.dto.MovimientoResponse;
import vetnova.inventario.exception.ResourceNotFoundException;
import vetnova.inventario.exception.StockInsuficienteException;
import vetnova.inventario.exception.TokenInvalidoException;
import vetnova.inventario.model.Producto;
import vetnova.inventario.model.Stock;
import vetnova.inventario.model.TipoMovimiento;
import vetnova.inventario.repository.MovimientoInventarioRepository;
import vetnova.inventario.repository.ProductoRepository;
import vetnova.inventario.repository.StockRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovimientoInventarioService - pruebas unitarias")
class MovimientoInventarioServiceTest {

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private MovimientoInventarioServiceImpl movimientoService;

    private Producto jeringas;
    private Stock stockExistente;

    @BeforeEach
    void setUp() {
        jeringas = Producto.builder().id(5L).nombre("Jeringas 5ml (caja x100)").build();
        stockExistente = Stock.builder()
                .id(1L)
                .productoId(5L)
                .sucursalId(2L)
                .cantidadDisponible(30)
                .cantidadReservada(0)
                .build();
    }

    @Nested
    @DisplayName("Registro de movimientos")
    class RegistroMovimientos {

        @Test
        @DisplayName("Una ENTRADA incrementa el stock disponible")
        void entradaIncrementaStock() {
            MovimientoRequest request = new MovimientoRequest(5L, 2L, TipoMovimiento.ENTRADA, 20, "Reposición", 1L, null);

            when(productoRepository.findById(5L)).thenReturn(Optional.of(jeringas));
            when(stockRepository.findByProductoIdAndSucursalId(5L, 2L)).thenReturn(Optional.of(stockExistente));
            when(movimientoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            MovimientoResponse response = movimientoService.registrarMovimiento(request, null);

            assertThat(response.getTipoMovimiento()).isEqualTo(TipoMovimiento.ENTRADA);
            assertThat(stockExistente.getCantidadDisponible()).isEqualTo(50);
            verify(stockRepository).save(stockExistente);
            verify(authClient, never()).tokenEsValido(any());
        }

        @Test
        @DisplayName("Una SALIDA descuenta el stock cuando hay unidades suficientes")
        void salidaDescuentaStockSuficiente() {
            MovimientoRequest request = new MovimientoRequest(5L, 2L, TipoMovimiento.SALIDA, 10, "Venta #100", 1L, "VENTA-100");

            when(productoRepository.findById(5L)).thenReturn(Optional.of(jeringas));
            when(stockRepository.findByProductoIdAndSucursalId(5L, 2L)).thenReturn(Optional.of(stockExistente));
            when(movimientoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            movimientoService.registrarMovimiento(request, null);

            assertThat(stockExistente.getCantidadDisponible()).isEqualTo(20);
        }

        @Test
        @DisplayName("Una SALIDA se rechaza si no hay stock suficiente")
        void salidaSeRechazaSinStockSuficiente() {
            MovimientoRequest request = new MovimientoRequest(5L, 2L, TipoMovimiento.SALIDA, 999, "Venta grande", 1L, null);

            when(productoRepository.findById(5L)).thenReturn(Optional.of(jeringas));
            when(stockRepository.findByProductoIdAndSucursalId(5L, 2L)).thenReturn(Optional.of(stockExistente));

            assertThatThrownBy(() -> movimientoService.registrarMovimiento(request, null))
                    .isInstanceOf(StockInsuficienteException.class)
                    .hasMessageContaining("Jeringas");

            verify(movimientoRepository, never()).save(any());
            verify(stockRepository, never()).save(any());
        }

        @Test
        @DisplayName("Crea una fila de stock nueva (en 0) si el producto no tenía stock previo en la sucursal")
        void creaStockNuevoSiNoExistia() {
            MovimientoRequest request = new MovimientoRequest(5L, 9L, TipoMovimiento.ENTRADA, 15, "Apertura sucursal", 1L, null);

            when(productoRepository.findById(5L)).thenReturn(Optional.of(jeringas));
            when(stockRepository.findByProductoIdAndSucursalId(5L, 9L)).thenReturn(Optional.empty());
            when(movimientoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Stock> captor = ArgumentCaptor.forClass(Stock.class);

            movimientoService.registrarMovimiento(request, null);

            verify(stockRepository).save(captor.capture());
            assertThat(captor.getValue().getCantidadDisponible()).isEqualTo(15);
            assertThat(captor.getValue().getSucursalId()).isEqualTo(9L);
        }

        @Test
        @DisplayName("Lanza excepción si el producto no existe")
        void lanzaExcepcionSiProductoNoExiste() {
            MovimientoRequest request = new MovimientoRequest(404L, 2L, TipoMovimiento.ENTRADA, 5, null, 1L, null);
            when(productoRepository.findById(404L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> movimientoService.registrarMovimiento(request, null))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Validación de token contra Autenticación")
    class ValidacionToken {

        @Test
        @DisplayName("Continúa con el registro si el token es válido")
        void continuaSiTokenValido() {
            MovimientoRequest request = new MovimientoRequest(5L, 2L, TipoMovimiento.ENTRADA, 5, null, 1L, null);

            when(authClient.tokenEsValido("token-valido")).thenReturn(true);
            when(productoRepository.findById(5L)).thenReturn(Optional.of(jeringas));
            when(stockRepository.findByProductoIdAndSucursalId(5L, 2L)).thenReturn(Optional.of(stockExistente));
            when(movimientoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            movimientoService.registrarMovimiento(request, "token-valido");

            verify(authClient).tokenEsValido("token-valido");
            verify(movimientoRepository).save(any());
        }

        @Test
        @DisplayName("Rechaza el registro si el token es inválido")
        void rechazaSiTokenInvalido() {
            MovimientoRequest request = new MovimientoRequest(5L, 2L, TipoMovimiento.ENTRADA, 5, null, 1L, null);

            when(authClient.tokenEsValido("token-vencido")).thenReturn(false);

            assertThatThrownBy(() -> movimientoService.registrarMovimiento(request, "token-vencido"))
                    .isInstanceOf(TokenInvalidoException.class);

            verify(productoRepository, never()).findById(any());
            verify(movimientoRepository, never()).save(any());
        }
    }
}
