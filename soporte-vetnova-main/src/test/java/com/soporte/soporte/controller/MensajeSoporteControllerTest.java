package com.soporte.soporte.controller;

import com.soporte.soporte.model.MensajeSoporte;
import com.soporte.soporte.model.Soporte;
import com.soporte.soporte.service.IMensajeSoporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MensajeSoporteControllerTest {

    @Mock
    private IMensajeSoporteService mensajeService;

    @InjectMocks
    private MensajeSoporteController mensajeSoporteController;

    private MensajeSoporte mensaje;

    @BeforeEach
    void setUp() {
        Soporte soporte = new Soporte();
        soporte.setId(1L);

        mensaje = new MensajeSoporte();
        mensaje.setId(1L);
        mensaje.setContenido("Necesito ayuda");
        mensaje.setSoporte(soporte);
    }

    @Test
    void agregar_debeRetornar201CuandoEsExitoso() {
        // GIVEN
        when(mensajeService.agregarMensaje(eq(1L), any(MensajeSoporte.class))).thenReturn(mensaje);

        // WHEN
        ResponseEntity<MensajeSoporte> response = mensajeSoporteController.agregar(1L, mensaje);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void listar_debeRetornar200ConLista() {
        // GIVEN
        when(mensajeService.listarMensajes(1L)).thenReturn(Arrays.asList(mensaje));

        // WHEN
        ResponseEntity<List<MensajeSoporte>> response = mensajeSoporteController.listar(1L);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void eliminar_debeRetornar204() {
        // WHEN
        ResponseEntity<Void> response = mensajeSoporteController.eliminar(1L, 1L);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }
}