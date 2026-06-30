package vetnova.inventario.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vetnova.inventario.dto.ItemVerificacionStock;
import vetnova.inventario.dto.StockResponse;
import vetnova.inventario.dto.VerificarStockRequest;
import vetnova.inventario.dto.VerificarStockResponse;
import vetnova.inventario.model.CategoriaProducto;
import vetnova.inventario.model.Producto;
import vetnova.inventario.model.Stock;
import vetnova.inventario.model.TipoUso;
import vetnova.inventario.repository.ProductoRepository;
import vetnova.inventario.repository.StockRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockService - pruebas unitarias")
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    private Producto alimentoPerro;
    private Stock stockSucursal1;

    @BeforeEach
    void setUp() {
        alimentoPerro = Producto.builder()
                .id(10L)
                .nombre("Alimento Premium Perro 15kg")
                .categoria(CategoriaProducto.ALIMENTO)
                .tipoUso(TipoUso.VENTA)
                .precio(new BigDecimal("45000"))
                .stockMinimo(5)
                .activo(true)
                .build();

        stockSucursal1 = Stock.builder()
                .id(100L)
                .productoId(10L)
                .sucursalId(1L)
                .cantidadDisponible(20)
                .cantidadReservada(5)
                .build();
    }

    @Nested
    @DisplayName("Consultas de stock")
    class Consultas {

        @Test
        @DisplayName("Lista el stock de una sucursal incluyendo el nombre del producto")
        void listaStockPorSucursal() {
            when(stockRepository.findBySucursalId(1L)).thenReturn(List.of(stockSucursal1));
            when(productoRepository.findById(10L)).thenReturn(Optional.of(alimentoPerro));

            List<StockResponse> resultado = stockService.listarPorSucursal(1L);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getNombreProducto()).isEqualTo("Alimento Premium Perro 15kg");
            assertThat(resultado.get(0).getCantidadDisponible()).isEqualTo(20);
        }

        @Test
        @DisplayName("Identifica productos bajo el stock mínimo")
        void identificaProductosBajoMinimo() {
            Stock stockBajo = Stock.builder().id(101L).productoId(10L).sucursalId(2L)
                    .cantidadDisponible(2).cantidadReservada(0).build();

            when(stockRepository.findAll()).thenReturn(List.of(stockSucursal1, stockBajo));
            when(productoRepository.findById(10L)).thenReturn(Optional.of(alimentoPerro));

            List<StockResponse> resultado = stockService.listarBajoMinimo();

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getSucursalId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("Verificación de disponibilidad (consumido por Ventas)")
    class VerificacionDisponibilidad {

        @Test
        @DisplayName("Marca como disponible cuando hay stock suficiente descontando lo reservado")
        void disponibleCuandoHayStockSuficiente() {
            // disponible real = 20 - 5 = 15, se piden 10
            ItemVerificacionStock item = new ItemVerificacionStock(10L, 10);
            VerificarStockRequest request = new VerificarStockRequest(1L, List.of(item));

            when(stockRepository.findByProductoIdAndSucursalId(10L, 1L)).thenReturn(Optional.of(stockSucursal1));

            VerificarStockResponse response = stockService.verificarDisponibilidad(request);

            assertThat(response.isDisponible()).isTrue();
            assertThat(response.getProductosSinStock()).isEmpty();
        }

        @Test
        @DisplayName("Marca como no disponible cuando la cantidad solicitada supera el stock libre")
        void noDisponibleCuandoFaltaStock() {
            // disponible real = 20 - 5 = 15, se piden 50
            ItemVerificacionStock item = new ItemVerificacionStock(10L, 50);
            VerificarStockRequest request = new VerificarStockRequest(1L, List.of(item));

            when(stockRepository.findByProductoIdAndSucursalId(10L, 1L)).thenReturn(Optional.of(stockSucursal1));
            when(productoRepository.findById(10L)).thenReturn(Optional.of(alimentoPerro));

            VerificarStockResponse response = stockService.verificarDisponibilidad(request);

            assertThat(response.isDisponible()).isFalse();
            assertThat(response.getProductosSinStock()).containsExactly("Alimento Premium Perro 15kg");
        }

        @Test
        @DisplayName("Trata como sin stock un producto que no tiene fila de stock en la sucursal")
        void productoSinFilaDeStockSeConsideraNoDisponible() {
            ItemVerificacionStock item = new ItemVerificacionStock(99L, 1);
            VerificarStockRequest request = new VerificarStockRequest(3L, List.of(item));

            when(stockRepository.findByProductoIdAndSucursalId(99L, 3L)).thenReturn(Optional.empty());
            when(productoRepository.findById(99L)).thenReturn(Optional.empty());

            VerificarStockResponse response = stockService.verificarDisponibilidad(request);

            assertThat(response.isDisponible()).isFalse();
            assertThat(response.getProductosSinStock()).containsExactly("Producto id 99");
        }

        @Test
        @DisplayName("Evalúa múltiples items y reporta solo los que faltan")
        void evaluaMultiplesItemsMixtos() {
            ItemVerificacionStock itemConStock = new ItemVerificacionStock(10L, 5);
            ItemVerificacionStock itemSinStock = new ItemVerificacionStock(20L, 3);
            VerificarStockRequest request = new VerificarStockRequest(1L, List.of(itemConStock, itemSinStock));

            Producto vacuna = Producto.builder().id(20L).nombre("Vacuna Triple Felina").build();
            Stock stockVacunaAgotado = Stock.builder().productoId(20L).sucursalId(1L)
                    .cantidadDisponible(1).cantidadReservada(0).build();

            when(stockRepository.findByProductoIdAndSucursalId(10L, 1L)).thenReturn(Optional.of(stockSucursal1));
            when(stockRepository.findByProductoIdAndSucursalId(20L, 1L)).thenReturn(Optional.of(stockVacunaAgotado));
            when(productoRepository.findById(20L)).thenReturn(Optional.of(vacuna));

            VerificarStockResponse response = stockService.verificarDisponibilidad(request);

            assertThat(response.isDisponible()).isFalse();
            assertThat(response.getProductosSinStock()).containsExactly("Vacuna Triple Felina");
        }
    }
}
