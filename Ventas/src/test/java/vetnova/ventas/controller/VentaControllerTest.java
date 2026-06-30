package vetnova.ventas.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vetnova.ventas.dto.ItemVentaRequest;
import vetnova.ventas.dto.PagarVentaRequest;
import vetnova.ventas.dto.VentaRequest;
import vetnova.ventas.dto.VentaResponse;
import vetnova.ventas.model.EstadoVenta;
import vetnova.ventas.model.MetodoPago;
import vetnova.ventas.service.VentaService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("VentaController - pruebas unitarias")
class VentaControllerTest {

    @Mock
    private VentaService ventaService;

    private VentaController controller;

    private VentaResponse ventaResponse;

    @BeforeEach
    void setUp() {
        controller = new VentaController(ventaService);
        ventaResponse = VentaResponse.builder().id(1L).estado(EstadoVenta.PENDIENTE).build();
    }

    @Test
    @DisplayName("listarTodas() responde 200")
    void listarTodas() {
        when(ventaService.listarTodas()).thenReturn(List.of(ventaResponse));

        ResponseEntity<List<VentaResponse>> response = controller.listarTodas();

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorId() delega en el servicio")
    void obtenerPorId() {
        when(ventaService.obtenerPorId(1L)).thenReturn(ventaResponse);

        controller.obtenerPorId(1L);

        verify(ventaService).obtenerPorId(1L);
    }

    @Test
    @DisplayName("listarPorCliente() delega en el servicio")
    void listarPorCliente() {
        when(ventaService.listarPorCliente(50L)).thenReturn(List.of(ventaResponse));

        ResponseEntity<List<VentaResponse>> response = controller.listarPorCliente(50L);

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("listarPorSucursal() delega en el servicio")
    void listarPorSucursal() {
        when(ventaService.listarPorSucursal(2L)).thenReturn(List.of());

        controller.listarPorSucursal(2L);

        verify(ventaService).listarPorSucursal(2L);
    }

    @Test
    @DisplayName("crearVentaDirecta() responde 201 CREATED")
    void crearVentaDirecta() {
        VentaRequest request = new VentaRequest(50L, 2L, 10L, MetodoPago.EFECTIVO, null, null,
                List.of(new ItemVentaRequest(7L, 1)));
        when(ventaService.crearVentaDirecta(request)).thenReturn(ventaResponse);

        ResponseEntity<VentaResponse> response = controller.crearVentaDirecta(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("confirmarCarrito() responde 201 CREATED")
    void confirmarCarrito() {
        when(ventaService.confirmarCarrito(5L)).thenReturn(ventaResponse);

        ResponseEntity<VentaResponse> response = controller.confirmarCarrito(5L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("pagar() delega en el servicio")
    void pagar() {
        PagarVentaRequest request = new PagarVentaRequest(MetodoPago.DEBITO);
        when(ventaService.pagar(1L, request)).thenReturn(ventaResponse);

        ResponseEntity<VentaResponse> response = controller.pagar(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("cancelar() delega en el servicio")
    void cancelar() {
        when(ventaService.cancelar(1L)).thenReturn(ventaResponse);

        controller.cancelar(1L);

        verify(ventaService).cancelar(1L);
    }
}
