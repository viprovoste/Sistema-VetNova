package com.vetnova.notificaciones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.notificaciones.dto.NotificacionRequestDTO;
import com.vetnova.notificaciones.model.Notificacion;
import com.vetnova.notificaciones.service.INotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificacionController.class)
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // En Spring Boot 3.4+, @MockitoBean reemplaza al antiguo @MockBean
    @MockitoBean
    private INotificacionService notificacionService;

    private Notificacion notificacion;
    private NotificacionRequestDTO dtoValido;

    @BeforeEach
    void setUp() {
        notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setTipo("EMAIL");
        notificacion.setDestinatario("usuario-1");
        notificacion.setAsunto("Recordatorio de cita");
        notificacion.setMensaje("Tiene una cita mañana");
        notificacion.setEstado("PENDIENTE");
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setIdCita(100L);

        dtoValido = new NotificacionRequestDTO();
        dtoValido.setAsunto("Recordatorio de cita");
        dtoValido.setDescripcion("Tiene una cita mañana");
        dtoValido.setEstado("PENDIENTE");
        dtoValido.setUsuarioId(1L);
        dtoValido.setIdCita(100L);
        dtoValido.setTipo("EMAIL");
    }

    // --- NUEVOS: TESTS PARA EL MÉTODO CREAR ---

    @Test
    void crearNotificacion_debeRetornar201CuandoEsExitoso() throws Exception {
        // GIVEN
        when(notificacionService.existeDuplicado(100L, "EMAIL", "PENDIENTE")).thenReturn(false);
        when(notificacionService.guardar(any(Notificacion.class))).thenReturn(notificacion);

        // WHEN + THEN
        mockMvc.perform(post("/api/notificaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.destinatario").value("usuario-1"));
    }

    @Test
    void crearNotificacion_debeRetornar409CuandoEsDuplicado() throws Exception {
        // GIVEN
        when(notificacionService.existeDuplicado(100L, "EMAIL", "PENDIENTE")).thenReturn(true);

        // WHEN + THEN
        mockMvc.perform(post("/api/notificaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoValido)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Notificación duplicada"));
    }

    // --- CORRECCIÓN DE TUS TESTS ANTERIORES USANDO MOCKMVC ---

    @Test
    void listarTodas_debeRetornar200ConLista() throws Exception {
        // GIVEN
        List<Notificacion> lista = Arrays.asList(notificacion);
        when(notificacionService.listarTodas()).thenReturn(lista);

        // WHEN + THEN
        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("EMAIL"));
    }

    @Test
    void obtenerPorId_debeRetornar200CuandoExiste() throws Exception {
        // GIVEN
        when(notificacionService.buscarPorId(1L)).thenReturn(Optional.of(notificacion));

        // WHEN + THEN
        mockMvc.perform(get("/api/notificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void obtenerPorId_debeRetornar404CuandoNoExiste() throws Exception {
        // GIVEN
        when(notificacionService.buscarPorId(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        mockMvc.perform(get("/api/notificaciones/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizar_debeRetornar200CuandoExiste() throws Exception {
        // GIVEN
        when(notificacionService.buscarPorId(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionService.guardar(any(Notificacion.class))).thenReturn(notificacion);

        // WHEN + THEN
        mockMvc.perform(put("/api/notificaciones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificacion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void actualizar_debeRetornar404CuandoNoExiste() throws Exception {
        // GIVEN
        when(notificacionService.buscarPorId(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        mockMvc.perform(put("/api/notificaciones/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificacion)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_debeRetornar204() throws Exception {
        // GIVEN (En tu controlador, el delete no verifica si existe antes, solo llama directo al service)
        // WHEN + THEN
        mockMvc.perform(delete("/api/notificaciones/1"))
                .andExpect(status().isNoContent());
    }
}