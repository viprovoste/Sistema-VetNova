package com.vetnova.agendamiento_sucursales.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.vetnova.agendamiento_sucursales.model.EstadoHora;
import com.vetnova.agendamiento_sucursales.model.HoraMedica;
import com.vetnova.agendamiento_sucursales.model.HoraMedicaDTO;
import com.vetnova.agendamiento_sucursales.service.HoraMedicaService;

import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(HoraMedicaController.class)
@ActiveProfiles("test")
public class HoraMedicaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HoraMedicaService horaMedicaService;

    @Autowired
    private ObjectMapper objectMapper;

    private HoraMedicaDTO crearDto() {
        HoraMedicaDTO dto = new HoraMedicaDTO();
        dto.setIdMascota(1L);
        dto.setIdCliente(1L);
        dto.setMotivoConsulta("Control general");
        dto.setFechaHora(LocalDateTime.of(2026, 1, 15, 10, 0));
        dto.setNombreVeterinario("Dr. House");
        dto.setIdBox(1L);
        return dto;
    }

    @Test
    void testpostHoraMedica() throws Exception {
        HoraMedicaDTO dto = crearDto();

        HoraMedica guardada = new HoraMedica();
        guardada.setIdHoraMedica(1L);
        guardada.setNombreMascota("Firulais");

        Mockito.when(horaMedicaService.guardarHoraMedica(any(HoraMedicaDTO.class))).thenReturn(guardada);

        mockMvc.perform(post("/api/horas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreMascota").value("Firulais"));
    }

    @Test
    void testpostHoraMedicaBadRequest() throws Exception {
        HoraMedicaDTO dto = crearDto();

        Mockito.when(horaMedicaService.guardarHoraMedica(any(HoraMedicaDTO.class))).thenReturn(null);

        mockMvc.perform(post("/api/horas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testpostHoraMedicaConflicto() throws Exception {
        HoraMedicaDTO dto = crearDto();

        Mockito.when(horaMedicaService.guardarHoraMedica(any(HoraMedicaDTO.class)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/horas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testgetHorasxNombreMascota() throws Exception {
        HoraMedica hora = new HoraMedica();
        hora.setNombreMascota("Firulais");

        Mockito.when(horaMedicaService.listarPorNombreMascota("Firulais")).thenReturn(java.util.List.of(hora));

        mockMvc.perform(get("/api/horas/buscar/mascota/Firulais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombreMascota", is("Firulais")));
    }

    @Test
    void testgetHorasxNombreMascotaVacio() throws Exception {
        Mockito.when(horaMedicaService.listarPorNombreMascota("Inexistente")).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/horas/buscar/mascota/Inexistente"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testgetHorasxNombreDueño() throws Exception {
        HoraMedica hora = new HoraMedica();
        hora.setNombreDueño("Juan Carlos");

        Mockito.when(horaMedicaService.listarPorNombreDueño("Juan Carlos")).thenReturn(java.util.List.of(hora));

        mockMvc.perform(get("/api/horas/buscar/dueño/Juan Carlos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreDueño", is("Juan Carlos")));
    }

    @Test
    void testgetHorasxNombreDueñoVacio() throws Exception {
        Mockito.when(horaMedicaService.listarPorNombreDueño("Inexistente")).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/horas/buscar/dueño/Inexistente"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testgetHorasxRut() throws Exception {
        HoraMedica hora = new HoraMedica();
        hora.setRutDueño("12345678-9");

        Mockito.when(horaMedicaService.listarPorRutDueño("12345678-9")).thenReturn(java.util.List.of(hora));

        mockMvc.perform(get("/api/horas/buscar/rut/12345678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rutDueño", is("12345678-9")));
    }

    @Test
    void testgetHorasxRutVacio() throws Exception {
        Mockito.when(horaMedicaService.listarPorRutDueño("00000000-0")).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/horas/buscar/rut/00000000-0"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testgetHorasxVeterinario() throws Exception {
        HoraMedica hora = new HoraMedica();
        hora.setNombreVeterinario("Dr. House");

        Mockito.when(horaMedicaService.listarPorVeterinario("Dr. House")).thenReturn(java.util.List.of(hora));

        mockMvc.perform(get("/api/horas/buscar/veterinario/Dr. House"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreVeterinario", is("Dr. House")));
    }

    @Test
    void testgetHorasxVeterinarioVacio() throws Exception {
        Mockito.when(horaMedicaService.listarPorVeterinario("Inexistente")).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/horas/buscar/veterinario/Inexistente"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testpatchHoraMedica() throws Exception {
        HoraMedicaDTO dto = new HoraMedicaDTO();
        dto.setMotivoConsulta("Vacunación");

        HoraMedica actualizado = new HoraMedica();
        actualizado.setIdHoraMedica(1L);
        actualizado.setMotivoConsulta("Vacunación");

        Mockito.when(horaMedicaService.actualizarHoraMedica(eq(1L), any(HoraMedicaDTO.class))).thenReturn(actualizado);

        mockMvc.perform(patch("/api/horas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivoConsulta").value("Vacunación"));
    }

    @Test
    void testpatchHoraMedicaNoExiste() throws Exception {
        HoraMedicaDTO dto = new HoraMedicaDTO();
        dto.setMotivoConsulta("Vacunación");

        Mockito.when(horaMedicaService.actualizarHoraMedica(eq(99L), any(HoraMedicaDTO.class))).thenReturn(null);

        mockMvc.perform(patch("/api/horas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testcancelarHoraMedica() throws Exception {
        HoraMedica cancelado = new HoraMedica();
        cancelado.setIdHoraMedica(1L);
        cancelado.setEstado(EstadoHora.CANCELADA);

        Mockito.when(horaMedicaService.cancelarHoraMedica(1L)).thenReturn(cancelado);

        mockMvc.perform(delete("/api/horas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("CANCELADA")));
    }

    @Test
    void testcancelarHoraMedicaNoExiste() throws Exception {
        Mockito.when(horaMedicaService.cancelarHoraMedica(99L)).thenReturn(null);

        mockMvc.perform(delete("/api/horas/99"))
                .andExpect(status().isNotFound());
    }

}