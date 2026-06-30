package vetnova.ventas.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import vetnova.ventas.dto.ActualizarCantidadRequest;
import vetnova.ventas.dto.CarritoResponse;
import vetnova.ventas.dto.ItemCarritoRequest;
import vetnova.ventas.model.EstadoCarrito;
import vetnova.ventas.service.CarritoService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarritoController - pruebas unitarias")
class CarritoControllerTest {

    @Mock
    private CarritoService carritoService;

    private CarritoController controller;

    private CarritoResponse carritoResponse;

    @BeforeEach
    void setUp() {
        controller = new CarritoController(carritoService);
        carritoResponse = CarritoResponse.builder().id(1L).clienteId(50L)
                .estado(EstadoCarrito.ACTIVO).items(List.of()).total(BigDecimal.ZERO).build();
    }

    @Test
    @DisplayName("obtenerCarritoActivo() delega en el servicio")
    void obtenerCarritoActivo() {
        when(carritoService.obtenerCarritoActivo(50L)).thenReturn(carritoResponse);

        ResponseEntity<CarritoResponse> response = controller.obtenerCarritoActivo(50L);

        assertThat(response.getBody().getClienteId()).isEqualTo(50L);
    }

    @Test
    @DisplayName("agregarItem() delega el request al servicio")
    void agregarItem() {
        ItemCarritoRequest request = new ItemCarritoRequest(7L, 2);
        when(carritoService.agregarItem(50L, request)).thenReturn(carritoResponse);

        controller.agregarItem(50L, request);

        verify(carritoService).agregarItem(50L, request);
    }

    @Test
    @DisplayName("actualizarCantidad() delega en el servicio")
    void actualizarCantidad() {
        ActualizarCantidadRequest request = new ActualizarCantidadRequest(5);
        when(carritoService.actualizarCantidad(1L, 20L, request)).thenReturn(carritoResponse);

        ResponseEntity<CarritoResponse> response = controller.actualizarCantidad(1L, 20L, request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @DisplayName("eliminarItem() delega en el servicio")
    void eliminarItem() {
        when(carritoService.eliminarItem(1L, 20L)).thenReturn(carritoResponse);

        controller.eliminarItem(1L, 20L);

        verify(carritoService).eliminarItem(1L, 20L);
    }

    @Test
    @DisplayName("vaciarCarrito() delega en el servicio")
    void vaciarCarrito() {
        when(carritoService.vaciarCarrito(1L)).thenReturn(carritoResponse);

        controller.vaciarCarrito(1L);

        verify(carritoService).vaciarCarrito(1L);
    }
}
