package vetnova.autentificacion.exception;

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

@DisplayName("GlobalExceptionHandler (Autenticación) - pruebas unitarias")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("ResourceNotFoundException se traduce a 404 NOT_FOUND")
    void manejaResourceNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(new ResourceNotFoundException("No existe"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("CredencialesInvalidasException se traduce a 401 UNAUTHORIZED")
    void manejaCredencialesInvalidas() {
        ResponseEntity<ErrorResponse> response = handler.handleCredenciales(
                new CredencialesInvalidasException("Credenciales incorrectas"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("EmailDuplicadoException se traduce a 409 CONFLICT")
    void manejaEmailDuplicado() {
        ResponseEntity<ErrorResponse> response = handler.handleEmailDuplicado(
                new EmailDuplicadoException("Email repetido"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("MethodArgumentNotValidException se traduce a 400 con el detalle de cada campo")
    void manejaErroresDeValidacion() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("usuarioRequest", "email", "El email no tiene un formato válido");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleValidacion(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getDetalles()).containsExactly("El email no tiene un formato válido");
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
