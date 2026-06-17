package com.vetnova.notificaciones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vetnova.notificaciones.model.Notificacion;
import com.vetnova.notificaciones.service.INotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionControllerTest {

    @Mock
    private INotificacionService notificacionService;

    @InjectMocks
    private NotificacionController notificacionController;

    private Notificacion notificacion;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setTipo("EMAIL");
        notificacion.setDestinatario("usuario-1");
        notificacion.setAsunto("Recordatorio de cita");
        notificacion.setMensaje("Tiene una cita mañana");
        notificacion.setEstado("PENDIENTE");
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setIdCita(100L);
    }

    @Test
    void listarTodas_debeRetornar200ConLista() {
        // GIVEN
        List<Notificacion> lista = Arrays.asList(notificacion);
        when(notificacionService.listarTodas()).thenReturn(lista);

        // WHEN
        ResponseEntity<List<Notificacion>> response = notificacionController.listarTodas();

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getTipo()).isEqualTo("EMAIL");
    }

    @Test
    void obtenerPorId_debeRetornar200CuandoExiste() {
        // GIVEN
        when(notificacionService.buscarPorId(1L)).thenReturn(Optional.of(notificacion));

        // WHEN
        ResponseEntity<Notificacion> response = notificacionController.obtenerPorId(1L);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getTipo()).isEqualTo("EMAIL");
    }

    @Test
    void obtenerPorId_debeRetornar404CuandoNoExiste() {
        // GIVEN
        when(notificacionService.buscarPorId(99L)).thenReturn(Optional.empty());

        // WHEN
        ResponseEntity<Notificacion> response = notificacionController.obtenerPorId(99L);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void actualizar_debeRetornar200CuandoExiste() {
        // GIVEN
        when(notificacionService.buscarPorId(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionService.guardar(any(Notificacion.class))).thenReturn(notificacion);

        // WHEN
        ResponseEntity<Notificacion> response = notificacionController.actualizar(1L, notificacion);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void actualizar_debeRetornar404CuandoNoExiste() {
        // GIVEN
        when(notificacionService.buscarPorId(99L)).thenReturn(Optional.empty());

        // WHEN
        ResponseEntity<Notificacion> response = notificacionController.actualizar(99L, notificacion);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void eliminar_debeRetornar204() {
        // GIVEN
        when(notificacionService.buscarPorId(1L)).thenReturn(Optional.of(notificacion));

        // WHEN
        ResponseEntity<Void> response = notificacionController.eliminar(1L);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }
}