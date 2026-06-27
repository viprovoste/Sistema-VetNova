package com.soporte.soporte.controller;

import com.soporte.soporte.model.Soporte;
import com.soporte.soporte.service.ISoporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SoporteControllerTest {

    @Mock
    private ISoporteService soporteService;

    @InjectMocks
    private SoporteController soporteController;

    private Soporte soporte;

    @BeforeEach
    void setUp() {
        soporte = new Soporte();
        soporte.setId(1L);
        soporte.setAsunto("Problema con login");
        soporte.setDescripcion("No puedo ingresar");
        soporte.setEstado("PENDIENTE");
        soporte.setUsuarioId(10L);
    }

    @Test
    void crearSoporte_debeRetornar201CuandoEsExitoso() {
        // GIVEN
        when(soporteService.existeDuplicado(10L, "Problema con login")).thenReturn(false);
        when(soporteService.guardar(any(Soporte.class))).thenReturn(soporte);

        // WHEN
        ResponseEntity<?> response = soporteController.crearSoporte(soporte);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(201);
    }

    @Test
    void crearSoporte_debeRetornar409CuandoExisteDuplicado() {
        // GIVEN
        when(soporteService.existeDuplicado(10L, "Problema con login")).thenReturn(true);

        // WHEN
        ResponseEntity<?> response = soporteController.crearSoporte(soporte);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(409);
    }

    @Test
    void listarTodos_debeRetornar200ConLista() {
        // GIVEN
        when(soporteService.listarTodos()).thenReturn(Arrays.asList(soporte));

        // WHEN
        ResponseEntity<List<Soporte>> response = soporteController.listarTodos();

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void obtenerPorId_debeRetornar200CuandoExiste() {
        // GIVEN
        when(soporteService.buscarPorId(1L)).thenReturn(Optional.of(soporte));

        // WHEN
        ResponseEntity<Soporte> response = soporteController.obtenerPorId(1L);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void obtenerPorId_debeRetornar404CuandoNoExiste() {
        // GIVEN
        when(soporteService.buscarPorId(99L)).thenReturn(Optional.empty());

        // WHEN
        ResponseEntity<Soporte> response = soporteController.obtenerPorId(99L);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void actualizar_debeRetornar200CuandoExiste() {
        // GIVEN
        when(soporteService.buscarPorId(1L)).thenReturn(Optional.of(soporte));
        when(soporteService.guardar(any(Soporte.class))).thenReturn(soporte);

        // WHEN
        ResponseEntity<Soporte> response = soporteController.actualizar(1L, soporte);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void actualizar_debeRetornar404CuandoNoExiste() {
        // GIVEN
        when(soporteService.buscarPorId(99L)).thenReturn(Optional.empty());

        // WHEN
        ResponseEntity<Soporte> response = soporteController.actualizar(99L, soporte);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void eliminar_debeRetornar204() {
        // WHEN
        ResponseEntity<Void> response = soporteController.eliminar(1L);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }
}