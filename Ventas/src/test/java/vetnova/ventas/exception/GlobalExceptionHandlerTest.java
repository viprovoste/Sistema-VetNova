package vetnova.ventas.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler (Ventas) - pruebas unitarias")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("ResourceNotFoundException se traduce a 404 NOT_FOUND")
    void manejaResourceNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(new ResourceNotFoundException("No existe"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("StockNoDisponibleException se traduce a 409 CONFLICT")
    void manejaStockNoDisponible() {
        ResponseEntity<ErrorResponse> response = handler.handleStockNoDisponible(
                new StockNoDisponibleException("Sin stock"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("VentaYaProcesadaException se traduce a 409 CONFLICT")
    void manejaVentaYaProcesada() {
        ResponseEntity<ErrorResponse> response = handler.handleVentaYaProcesada(
                new VentaYaProcesadaException("Ya procesada"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("ServicioExternoException se traduce a 503 SERVICE_UNAVAILABLE")
    void manejaServicioExterno() {
        ResponseEntity<ErrorResponse> response = handler.handleServicioExterno(
                new ServicioExternoException("Inventario no responde"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    @DisplayName("MethodArgumentNotValidException se traduce a 400 con el detalle de cada campo")
    void manejaErroresDeValidacion() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("ventaRequest", "sucursalId", "La sucursal es obligatoria");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleValidacion(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getDetalles()).containsExactly("La sucursal es obligatoria");
    }

    @Test
    @DisplayName("IllegalArgumentException se traduce a 400 BAD_REQUEST")
    void manejaIllegalArgument() {
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(
                new IllegalArgumentException("inválido"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Cualquier otra excepción se traduce a 500 INTERNAL_SERVER_ERROR")
    void manejaExcepcionGeneral() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneral(new RuntimeException("boom"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
