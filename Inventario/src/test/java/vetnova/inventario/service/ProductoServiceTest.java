package vetnova.inventario.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vetnova.inventario.dto.ProductoRequest;
import vetnova.inventario.dto.ProductoResponse;
import vetnova.inventario.exception.ResourceNotFoundException;
import vetnova.inventario.exception.SkuDuplicadoException;
import vetnova.inventario.model.CategoriaProducto;
import vetnova.inventario.model.Producto;
import vetnova.inventario.model.TipoUso;
import vetnova.inventario.repository.ProductoRepository;

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
@DisplayName("ProductoService - pruebas unitarias")
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto vacunaRabia;
    private ProductoRequest requestVacuna;

    @BeforeEach
    void setUp() {
        vacunaRabia = Producto.builder()
                .id(1L)
                .nombre("Vacuna Antirrábica")
                .descripcion("Vacuna anual para perros y gatos")
                .categoria(CategoriaProducto.MEDICAMENTO)
                .tipoUso(TipoUso.USO_CLINICO)
                .precio(new BigDecimal("12000"))
                .codigoSku("VAC-RAB-001")
                .unidadMedida("dosis")
                .stockMinimo(10)
                .activo(true)
                .build();

        requestVacuna = new ProductoRequest();
        requestVacuna.setNombre("Vacuna Antirrábica");
        requestVacuna.setDescripcion("Vacuna anual para perros y gatos");
        requestVacuna.setCategoria(CategoriaProducto.MEDICAMENTO);
        requestVacuna.setTipoUso(TipoUso.USO_CLINICO);
        requestVacuna.setPrecio(new BigDecimal("12000"));
        requestVacuna.setCodigoSku("VAC-RAB-001");
        requestVacuna.setUnidadMedida("dosis");
        requestVacuna.setStockMinimo(10);
    }

    @Nested
    @DisplayName("Creación de productos")
    class Crear {

        @Test
        @DisplayName("Crea un producto cuando el SKU no está en uso")
        void creaProductoCorrectamente() {
            when(productoRepository.existsByCodigoSku("VAC-RAB-001")).thenReturn(false);
            when(productoRepository.save(any(Producto.class))).thenReturn(vacunaRabia);

            ProductoResponse response = productoService.crear(requestVacuna);

            assertThat(response.getNombre()).isEqualTo("Vacuna Antirrábica");
            assertThat(response.getCategoria()).isEqualTo(CategoriaProducto.MEDICAMENTO);
            verify(productoRepository).save(any(Producto.class));
        }

        @Test
        @DisplayName("Lanza excepción si el SKU ya existe")
        void lanzaExcepcionSiSkuDuplicado() {
            when(productoRepository.existsByCodigoSku("VAC-RAB-001")).thenReturn(true);

            assertThatThrownBy(() -> productoService.crear(requestVacuna))
                    .isInstanceOf(SkuDuplicadoException.class)
                    .hasMessageContaining("VAC-RAB-001");

            verify(productoRepository, never()).save(any(Producto.class));
        }
    }

    @Nested
    @DisplayName("Consultas")
    class Consultas {

        @Test
        @DisplayName("Obtiene un producto existente por ID")
        void obtienePorIdExistente() {
            when(productoRepository.findById(1L)).thenReturn(Optional.of(vacunaRabia));

            ProductoResponse response = productoService.obtenerPorId(1L);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getCodigoSku()).isEqualTo("VAC-RAB-001");
        }

        @Test
        @DisplayName("Lanza excepción si el producto no existe")
        void lanzaExcepcionSiProductoNoExiste() {
            when(productoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productoService.obtenerPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Filtra productos por categoría")
        void listaPorCategoria() {
            when(productoRepository.findByCategoria(CategoriaProducto.MEDICAMENTO))
                    .thenReturn(List.of(vacunaRabia));

            List<ProductoResponse> resultado = productoService.listarPorCategoria(CategoriaProducto.MEDICAMENTO);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getCategoria()).isEqualTo(CategoriaProducto.MEDICAMENTO);
        }

        @Test
        @DisplayName("Filtra productos por tipo de uso")
        void listaPorTipoUso() {
            when(productoRepository.findByTipoUso(TipoUso.USO_CLINICO)).thenReturn(List.of(vacunaRabia));

            List<ProductoResponse> resultado = productoService.listarPorTipoUso(TipoUso.USO_CLINICO);

            assertThat(resultado).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Actualización y desactivación")
    class ActualizacionYBaja {

        @Test
        @DisplayName("Actualiza un producto sin cambiar el SKU")
        void actualizaProductoSinCambiarSku() {
            requestVacuna.setPrecio(new BigDecimal("13500"));
            when(productoRepository.findById(1L)).thenReturn(Optional.of(vacunaRabia));
            when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

            ProductoResponse response = productoService.actualizar(1L, requestVacuna);

            assertThat(response.getPrecio()).isEqualByComparingTo("13500");
            verify(productoRepository, never()).existsByCodigoSku(any());
        }

        @Test
        @DisplayName("Rechaza la actualización si el nuevo SKU ya pertenece a otro producto")
        void rechazaActualizacionPorSkuDuplicado() {
            requestVacuna.setCodigoSku("OTRO-SKU");
            when(productoRepository.findById(1L)).thenReturn(Optional.of(vacunaRabia));
            when(productoRepository.existsByCodigoSku("OTRO-SKU")).thenReturn(true);

            assertThatThrownBy(() -> productoService.actualizar(1L, requestVacuna))
                    .isInstanceOf(SkuDuplicadoException.class);

            verify(productoRepository, never()).save(any(Producto.class));
        }

        @Test
        @DisplayName("Desactiva un producto (soft delete)")
        void desactivaProducto() {
            when(productoRepository.findById(1L)).thenReturn(Optional.of(vacunaRabia));
            when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

            ProductoResponse response = productoService.desactivar(1L);

            assertThat(response.getActivo()).isFalse();
        }
    }
}
