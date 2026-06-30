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
import vetnova.ventas.client.dto.ProductoDTO;
import vetnova.ventas.client.dto.VerificarStockRequestDTO;
import vetnova.ventas.client.dto.VerificarStockResponseDTO;
import vetnova.ventas.dto.ActualizarCantidadRequest;
import vetnova.ventas.dto.CarritoResponse;
import vetnova.ventas.dto.ItemCarritoRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarritoService - pruebas unitarias")
class CarritoServiceTest {

    private static final Long SUCURSAL_WEB_ID = 1L;

    @Mock
    private CarritoCompraRepository carritoRepository;

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private InventarioClient inventarioClient;

    private CarritoServiceImpl carritoService;

    private CarritoCompra carritoActivo;
    private ProductoDTO alimentoGato;

    @BeforeEach
    void setUp() {
        carritoService = new CarritoServiceImpl(carritoRepository, itemCarritoRepository, inventarioClient, SUCURSAL_WEB_ID);

        carritoActivo = CarritoCompra.builder()
                .id(1L)
                .clienteId(50L)
                .estado(EstadoCarrito.ACTIVO)
                .build();

        alimentoGato = new ProductoDTO(7L, "Alimento Gato Adulto 10kg", new BigDecimal("38000"), true);
    }

    @Nested
    @DisplayName("Obtener carrito activo")
    class ObtenerCarritoActivo {

        @Test
        @DisplayName("Reutiliza el carrito activo existente del cliente")
        void reutilizaCarritoExistente() {
            when(carritoRepository.findByClienteIdAndEstado(50L, EstadoCarrito.ACTIVO))
                    .thenReturn(Optional.of(carritoActivo));
            when(itemCarritoRepository.findByCarritoId(1L)).thenReturn(List.of());

            CarritoResponse response = carritoService.obtenerCarritoActivo(50L);

            assertThat(response.getId()).isEqualTo(1L);
            verify(carritoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Crea un carrito nuevo si el cliente no tiene uno activo")
        void creaCarritoNuevoSiNoExiste() {
            when(carritoRepository.findByClienteIdAndEstado(99L, EstadoCarrito.ACTIVO)).thenReturn(Optional.empty());
            when(carritoRepository.save(any(CarritoCompra.class))).thenAnswer(inv -> {
                CarritoCompra c = inv.getArgument(0);
                c.setId(5L);
                return c;
            });
            when(itemCarritoRepository.findByCarritoId(5L)).thenReturn(List.of());

            CarritoResponse response = carritoService.obtenerCarritoActivo(99L);

            assertThat(response.getId()).isEqualTo(5L);
            assertThat(response.getEstado()).isEqualTo(EstadoCarrito.ACTIVO);
        }
    }

    @Nested
    @DisplayName("Agregar producto al carrito")
    class AgregarItem {

        @Test
        @DisplayName("Agrega un producto nuevo cuando hay stock suficiente")
        void agregaProductoNuevoConStock() {
            ItemCarritoRequest request = new ItemCarritoRequest(7L, 2);

            when(carritoRepository.findByClienteIdAndEstado(50L, EstadoCarrito.ACTIVO))
                    .thenReturn(Optional.of(carritoActivo));
            when(inventarioClient.obtenerProducto(7L)).thenReturn(alimentoGato);
            when(itemCarritoRepository.findByCarritoIdAndProductoId(1L, 7L)).thenReturn(Optional.empty());
            when(inventarioClient.verificarDisponibilidad(any()))
                    .thenReturn(new VerificarStockResponseDTO(true, List.of()));
            when(itemCarritoRepository.findByCarritoId(1L)).thenReturn(List.of());

            carritoService.agregarItem(50L, request);

            ArgumentCaptor<ItemCarrito> captor = ArgumentCaptor.forClass(ItemCarrito.class);
            verify(itemCarritoRepository).save(captor.capture());
            assertThat(captor.getValue().getNombreProducto()).isEqualTo("Alimento Gato Adulto 10kg");
            assertThat(captor.getValue().getCantidad()).isEqualTo(2);
        }

        @Test
        @DisplayName("Suma la cantidad si el producto ya estaba en el carrito")
        void sumaCantidadSiProductoYaEstaba() {
            ItemCarritoRequest request = new ItemCarritoRequest(7L, 3);
            ItemCarrito itemExistente = ItemCarrito.builder()
                    .id(20L).carritoId(1L).productoId(7L)
                    .nombreProducto("Alimento Gato Adulto 10kg")
                    .precioUnitario(new BigDecimal("38000"))
                    .cantidad(2)
                    .build();

            when(carritoRepository.findByClienteIdAndEstado(50L, EstadoCarrito.ACTIVO))
                    .thenReturn(Optional.of(carritoActivo));
            when(inventarioClient.obtenerProducto(7L)).thenReturn(alimentoGato);
            when(itemCarritoRepository.findByCarritoIdAndProductoId(1L, 7L)).thenReturn(Optional.of(itemExistente));
            when(inventarioClient.verificarDisponibilidad(any()))
                    .thenReturn(new VerificarStockResponseDTO(true, List.of()));
            when(itemCarritoRepository.findByCarritoId(1L)).thenReturn(List.of(itemExistente));

            carritoService.agregarItem(50L, request);

            assertThat(itemExistente.getCantidad()).isEqualTo(5);
            verify(itemCarritoRepository).save(itemExistente);
        }

        @Test
        @DisplayName("Rechaza agregar el producto si Inventario indica que no hay stock")
        void rechazaSiNoHayStock() {
            ItemCarritoRequest request = new ItemCarritoRequest(7L, 100);

            when(carritoRepository.findByClienteIdAndEstado(50L, EstadoCarrito.ACTIVO))
                    .thenReturn(Optional.of(carritoActivo));
            when(inventarioClient.obtenerProducto(7L)).thenReturn(alimentoGato);
            when(itemCarritoRepository.findByCarritoIdAndProductoId(1L, 7L)).thenReturn(Optional.empty());
            when(inventarioClient.verificarDisponibilidad(any()))
                    .thenReturn(new VerificarStockResponseDTO(false, List.of("Alimento Gato Adulto 10kg")));

            assertThatThrownBy(() -> carritoService.agregarItem(50L, request))
                    .isInstanceOf(StockNoDisponibleException.class)
                    .hasMessageContaining("Alimento Gato Adulto 10kg");

            verify(itemCarritoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Rechaza agregar un producto que Inventario marca como inactivo")
        void rechazaProductoInactivo() {
            ProductoDTO productoInactivo = new ProductoDTO(8L, "Collar descontinuado", new BigDecimal("5000"), false);
            ItemCarritoRequest request = new ItemCarritoRequest(8L, 1);

            when(carritoRepository.findByClienteIdAndEstado(50L, EstadoCarrito.ACTIVO))
                    .thenReturn(Optional.of(carritoActivo));
            when(inventarioClient.obtenerProducto(8L)).thenReturn(productoInactivo);

            assertThatThrownBy(() -> carritoService.agregarItem(50L, request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(inventarioClient, never()).verificarDisponibilidad(any());
        }

        @Test
        @DisplayName("Verifica disponibilidad usando la cantidad TOTAL deseada (existente + nueva)")
        void verificaConCantidadTotalDeseada() {
            ItemCarritoRequest request = new ItemCarritoRequest(7L, 3);
            ItemCarrito itemExistente = ItemCarrito.builder()
                    .id(20L).carritoId(1L).productoId(7L)
                    .nombreProducto("Alimento Gato Adulto 10kg")
                    .precioUnitario(new BigDecimal("38000"))
                    .cantidad(4)
                    .build();

            when(carritoRepository.findByClienteIdAndEstado(50L, EstadoCarrito.ACTIVO))
                    .thenReturn(Optional.of(carritoActivo));
            when(inventarioClient.obtenerProducto(7L)).thenReturn(alimentoGato);
            when(itemCarritoRepository.findByCarritoIdAndProductoId(1L, 7L)).thenReturn(Optional.of(itemExistente));
            when(inventarioClient.verificarDisponibilidad(any()))
                    .thenReturn(new VerificarStockResponseDTO(true, List.of()));
            when(itemCarritoRepository.findByCarritoId(1L)).thenReturn(List.of(itemExistente));

            carritoService.agregarItem(50L, request);

            ArgumentCaptor<VerificarStockRequestDTO> captor = ArgumentCaptor.forClass(VerificarStockRequestDTO.class);
            verify(inventarioClient).verificarDisponibilidad(captor.capture());
            assertThat(captor.getValue().getItems().get(0).getCantidad()).isEqualTo(7); // 4 existentes + 3 nuevas
            assertThat(captor.getValue().getSucursalId()).isEqualTo(SUCURSAL_WEB_ID);
        }
    }

    @Nested
    @DisplayName("Actualizar, eliminar y vaciar")
    class ActualizarEliminarVaciar {

        @Test
        @DisplayName("Actualiza la cantidad de un item existente")
        void actualizaCantidad() {
            ItemCarrito item = ItemCarrito.builder().id(20L).carritoId(1L).productoId(7L)
                    .precioUnitario(new BigDecimal("38000")).cantidad(2).build();

            when(carritoRepository.findById(1L)).thenReturn(Optional.of(carritoActivo));
            when(itemCarritoRepository.findById(20L)).thenReturn(Optional.of(item));
            when(inventarioClient.verificarDisponibilidad(any()))
                    .thenReturn(new VerificarStockResponseDTO(true, List.of()));
            when(itemCarritoRepository.findByCarritoId(1L)).thenReturn(List.of(item));

            carritoService.actualizarCantidad(1L, 20L, new ActualizarCantidadRequest(6));

            assertThat(item.getCantidad()).isEqualTo(6);
        }

        @Test
        @DisplayName("Rechaza operar sobre un item que no pertenece al carrito indicado")
        void rechazaItemDeOtroCarrito() {
            ItemCarrito itemDeOtroCarrito = ItemCarrito.builder().id(20L).carritoId(99L).build();

            when(carritoRepository.findById(1L)).thenReturn(Optional.of(carritoActivo));
            when(itemCarritoRepository.findById(20L)).thenReturn(Optional.of(itemDeOtroCarrito));

            assertThatThrownBy(() -> carritoService.eliminarItem(1L, 20L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Elimina un item del carrito")
        void eliminaItem() {
            ItemCarrito item = ItemCarrito.builder().id(20L).carritoId(1L).build();

            when(carritoRepository.findById(1L)).thenReturn(Optional.of(carritoActivo));
            when(itemCarritoRepository.findById(20L)).thenReturn(Optional.of(item));
            when(itemCarritoRepository.findByCarritoId(1L)).thenReturn(List.of());

            carritoService.eliminarItem(1L, 20L);

            verify(itemCarritoRepository).delete(item);
        }

        @Test
        @DisplayName("Vacía todos los items de un carrito")
        void vaciaCarrito() {
            when(carritoRepository.findById(1L)).thenReturn(Optional.of(carritoActivo));
            when(itemCarritoRepository.findByCarritoId(1L)).thenReturn(List.of());

            CarritoResponse response = carritoService.vaciarCarrito(1L);

            verify(itemCarritoRepository).deleteByCarritoId(1L);
            assertThat(response.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }
}
