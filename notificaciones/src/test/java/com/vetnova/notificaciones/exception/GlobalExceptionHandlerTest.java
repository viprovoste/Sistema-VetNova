package com.vetnova.notificaciones.exception;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleValidationErrors() {
        // GIVEN
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        FieldError fieldError = new FieldError("notificacion", "destino", "El destino es obligatorio");
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));
        
        MethodParameter parameter = Mockito.mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        // WHEN
        ResponseEntity<Map<String, Object>> response = handler.handleValidationErrors(ex);

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error de validación", response.getBody().get("error"));
    }

    @Test
    void testHandleConstraintViolation() {
        // GIVEN
        ConstraintViolationException ex = new ConstraintViolationException("Violación detectada", null);

        // WHEN
        ResponseEntity<Map<String, Object>> response = handler.handleConstraintViolation(ex);

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Violación de restricción", response.getBody().get("error"));
    }

    @Test
    void testHandleRuntimeException() {
        // GIVEN
        RuntimeException ex = new RuntimeException("Error en tiempo de ejecución");

        // WHEN
        ResponseEntity<Map<String, Object>> response = handler.handleRuntimeException(ex);

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error interno del servidor", response.getBody().get("error"));
    }

    @Test
    void testHandleGenericException() {
        // GIVEN
        Exception ex = new Exception("Error general");

        // WHEN
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error inesperado", response.getBody().get("error"));
    }
}