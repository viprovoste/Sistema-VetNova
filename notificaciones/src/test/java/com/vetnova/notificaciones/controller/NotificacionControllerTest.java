package com.vetnova.notificaciones.controller;

import com.vetnova.notificaciones.dto.NotificacionRequestDTO;
import com.vetnova.notificaciones.dto.SoporteDTO;
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
    private NotificacionRequestDTO dto;

    @BeforeEach
    void setUp() {
        // Inicializamos la Entidad
        notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setTipo("EMAIL");
        notificacion.setDestinatario("usuario-1");
        notificacion.setAsunto("Recordatorio de cita");
        notificacion.setMensaje("Tiene una cita mañana");
        notificacion.setEstado("PENDIENTE");
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setIdCita(100L);

        // Inicializamos el DTO que espera el controlador
        dto = new NotificacionRequestDTO();
        dto.setAsunto("Recordatorio de cita");
        dto.setDescripcion("Tiene una cita mañana");
        dto.setEstado("PENDIENTE");
        dto.setUsuarioId(1L);
        dto.setIdCita(100L);
        dto.setTipo("EMAIL");
    }

    // ==========================================
    // HU-01: CREAR NOTIFICACIÓN (¡Corregido!)
    // ==========================================
    @Test
    void crearNotificacion_debeRetornar201CuandoEsExitoso() {
        // GIVEN: No es duplicado y el servicio guarda exitosamente
        when(notificacionService.existeDuplicado(100L, "EMAIL", "PENDIENTE")).thenReturn(false);
        when(notificacionService.guardar(any(Notificacion.class))).thenReturn(notificacion);

        // WHEN
        ResponseEntity<?> response = notificacionController.crearNotificacion(dto);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        Notificacion body = (Notificacion) response.getBody();
        assertThat(body.getId()).isEqualTo(1L);
    }

    @Test
    void crearNotificacion_debeRetornar409CuandoExisteDuplicado() {
        // GIVEN: El servicio dice que SÍ es un duplicado
        when(notificacionService.existeDuplicado(100L, "EMAIL", "PENDIENTE")).thenReturn(true);

        // WHEN
        ResponseEntity<?> response = notificacionController.crearNotificacion(dto);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(409);
    }

    // ==========================================
    // HU-02: LISTAR TODAS
    // ==========================================
    @Test
    void listarTodas_debeRetornar200ConLista() {
        List<Notificacion> lista = Arrays.asList(notificacion);
        when(notificacionService.listarTodas()).thenReturn(lista);

        ResponseEntity<List<Notificacion>> response = notificacionController.listarTodas();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
    }

    // ==========================================
    // HU-03: OBTENER POR ID
    // ==========================================
    @Test
    void obtenerPorId_debeRetornar200CuandoExiste() {
        when(notificacionService.buscarPorId(1L)).thenReturn(Optional.of(notificacion));

        ResponseEntity<Notificacion> response = notificacionController.obtenerPorId(1L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void obtenerPorId_debeRetornar404CuandoNoExiste() {
        when(notificacionService.buscarPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<Notificacion> response = notificacionController.obtenerPorId(99L);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    // ==========================================
    // HU-04: ACTUALIZAR
    // ==========================================
    @Test
    void actualizar_debeRetornar200CuandoExiste() {
        when(notificacionService.buscarPorId(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionService.guardar(any(Notificacion.class))).thenReturn(notificacion);

        ResponseEntity<Notificacion> response = notificacionController.actualizar(1L, notificacion);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void actualizar_debeRetornar404CuandoNoExiste() {
        when(notificacionService.buscarPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<Notificacion> response = notificacionController.actualizar(99L, notificacion);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    // ==========================================
    // HU-05: ELIMINAR
    // ==========================================
    @Test
    void eliminar_debeRetornar204() {
        ResponseEntity<Void> response = notificacionController.eliminar(1L);
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
void testSoporteDTO() {
    // GIVEN
    SoporteDTO dto = new SoporteDTO();
    
    // WHEN
    dto.setAsunto("Problema con inicio de sesión");
    dto.setDescripcion("No puedo entrar a mi cuenta desde la app");
    dto.setEstado("PENDIENTE");
    dto.setUsuarioId(10L);
    
    // THEN
    org.junit.jupiter.api.Assertions.assertEquals("Problema con inicio de sesión", dto.getAsunto());
    org.junit.jupiter.api.Assertions.assertEquals("No puedo entrar a mi cuenta desde la app", dto.getDescripcion());
    org.junit.jupiter.api.Assertions.assertEquals("PENDIENTE", dto.getEstado());
    org.junit.jupiter.api.Assertions.assertEquals(10L, dto.getUsuarioId());
}
}