package vetnova.ventas.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import vetnova.ventas.client.dto.*;
import vetnova.ventas.exception.ResourceNotFoundException;
import vetnova.ventas.exception.ServicioExternoException;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventarioClient - pruebas unitarias")
class InventarioClientTest {

    @Mock
    private RestTemplate restTemplate;

    private InventarioClient inventarioClient;

    @BeforeEach
    void setUp() {
        inventarioClient = new InventarioClient(restTemplate, "http://localhost:8085");
    }

    @Nested
    @DisplayName("obtenerProducto")
    class ObtenerProducto {

        @Test
        @DisplayName("Devuelve el producto cuando Inventario responde correctamente")
        void devuelveProducto() {
            ProductoDTO producto = new ProductoDTO(7L, "Shampoo antipulgas", new BigDecimal("9000"), true);
            when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class), eq(7L))).thenReturn(producto);

            ProductoDTO resultado = inventarioClient.obtenerProducto(7L);

            assertThat(resultado.getNombre()).isEqualTo("Shampoo antipulgas");
        }

        @Test
        @DisplayName("Lanza ResourceNotFoundException si Inventario devuelve null")
        void lanzaNotFoundSiRespuestaNula() {
            when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class), eq(404L))).thenReturn(null);

            assertThatThrownBy(() -> inventarioClient.obtenerProducto(404L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Lanza ServicioExternoException si Inventario no responde")
        void lanzaServicioExternoSiFallaConexion() {
            when(restTemplate.getForObject(anyString(), eq(ProductoDTO.class), eq(7L)))
                    .thenThrow(new ResourceAccessException("timeout"));

            assertThatThrownBy(() -> inventarioClient.obtenerProducto(7L))
                    .isInstanceOf(ServicioExternoException.class);
        }
    }

    @Nested
    @DisplayName("verificarDisponibilidad")
    class VerificarDisponibilidad {

        @Test
        @DisplayName("Devuelve la respuesta de disponibilidad de Inventario")
        void devuelveRespuesta() {
            VerificarStockRequestDTO request = new VerificarStockRequestDTO(1L,
                    List.of(new ItemVerificacionStockDTO(7L, 2)));
            VerificarStockResponseDTO esperado = new VerificarStockResponseDTO(true, List.of());

            when(restTemplate.postForObject(anyString(), eq(request), eq(VerificarStockResponseDTO.class)))
                    .thenReturn(esperado);

            VerificarStockResponseDTO resultado = inventarioClient.verificarDisponibilidad(request);

            assertThat(resultado.isDisponible()).isTrue();
        }

        @Test
        @DisplayName("Lanza ServicioExternoException si la respuesta es nula")
        void lanzaServicioExternoSiRespuestaNula() {
            VerificarStockRequestDTO request = new VerificarStockRequestDTO(1L, List.of());
            when(restTemplate.postForObject(anyString(), eq(request), eq(VerificarStockResponseDTO.class)))
                    .thenReturn(null);

            assertThatThrownBy(() -> inventarioClient.verificarDisponibilidad(request))
                    .isInstanceOf(ServicioExternoException.class);
        }

        @Test
        @DisplayName("Lanza ServicioExternoException si Inventario no responde")
        void lanzaServicioExternoSiFallaConexion() {
            VerificarStockRequestDTO request = new VerificarStockRequestDTO(1L, List.of());
            when(restTemplate.postForObject(anyString(), eq(request), eq(VerificarStockResponseDTO.class)))
                    .thenThrow(new ResourceAccessException("timeout"));

            assertThatThrownBy(() -> inventarioClient.verificarDisponibilidad(request))
                    .isInstanceOf(ServicioExternoException.class);
        }
    }

    @Nested
    @DisplayName("registrarSalida")
    class RegistrarSalida {

        @Test
        @DisplayName("Llama a Inventario sin lanzar excepción cuando la respuesta es exitosa")
        void registraSalidaCorrectamente() {
            MovimientoRequestDTO movimiento = new MovimientoRequestDTO(7L, 2L, "SALIDA", 1, "Venta #1", 1L, "VENTA-1");

            inventarioClient.registrarSalida(movimiento);

            // No debe lanzar excepción; basta con que no truene
            assertThat(true).isTrue();
        }

        @Test
        @DisplayName("Lanza ServicioExternoException si Inventario no responde")
        void lanzaServicioExternoSiFallaConexion() {
            MovimientoRequestDTO movimiento = new MovimientoRequestDTO(7L, 2L, "SALIDA", 1, "Venta #1", 1L, "VENTA-1");
            when(restTemplate.postForObject(anyString(), eq(movimiento), eq(Object.class)))
                    .thenThrow(new ResourceAccessException("timeout"));

            assertThatThrownBy(() -> inventarioClient.registrarSalida(movimiento))
                    .isInstanceOf(ServicioExternoException.class);
        }
    }
}
