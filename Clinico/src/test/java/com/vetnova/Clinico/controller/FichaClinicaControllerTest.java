package com.vetnova.Clinico.controller;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.vetnova.Clinico.model.FichaClinica;
import com.vetnova.Clinico.model.FichaClinicaDTO;
import com.vetnova.Clinico.service.FichaClinicaService;

import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@WebMvcTest(FichaClinicaController.class)
@ActiveProfiles("test")
public class FichaClinicaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FichaClinicaService fichaClinicaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testpostFichaClinica() throws Exception {
        FichaClinicaDTO dto = new FichaClinicaDTO();
        dto.setIdMascota(1L);
        dto.setIdCliente(1L);

        FichaClinica guardada = new FichaClinica();
        guardada.setIdFicha(1L);
        guardada.setNombreMascota("Firulais");

        Mockito.when(fichaClinicaService.guardarFichaClinica(any(FichaClinicaDTO.class))).thenReturn(guardada);

        mockMvc.perform(post("/api/ficha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreMascota").value("Firulais"));
    }

    @Test
    void testpostFichaClinicaConflicto() throws Exception {
        FichaClinicaDTO dto = new FichaClinicaDTO();
        dto.setIdMascota(1L);
        dto.setIdCliente(1L);

        Mockito.when(fichaClinicaService.guardarFichaClinica(any(FichaClinicaDTO.class)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/ficha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testgetFichasClinicas() throws Exception {
        FichaClinica f1 = new FichaClinica();
        f1.setIdFicha(1L);
        f1.setNombreMascota("Firulais");

        FichaClinica f2 = new FichaClinica();
        f2.setIdFicha(2L);
        f2.setNombreMascota("Pelusa");

        Mockito.when(fichaClinicaService.listarFichasClinicas()).thenReturn(Arrays.asList(f1, f2));

        mockMvc.perform(get("/api/ficha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombreMascota", is("Firulais")))
                .andExpect(jsonPath("$[1].nombreMascota", is("Pelusa")));
    }

    @Test
    void testgetFichaClinicaxId() throws Exception {
        FichaClinica buscado = new FichaClinica();
        buscado.setIdFicha(1L);
        buscado.setNombreMascota("Firulais");

        Mockito.when(fichaClinicaService.findById(1L)).thenReturn(Optional.of(buscado));

        mockMvc.perform(get("/api/ficha/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreMascota").value("Firulais"));
    }

    @Test
    void testgetFichaClinicaxIdNoExistente() throws Exception {
        Mockito.when(fichaClinicaService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ficha/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testgetFichaClinicaxNombreMascota() throws Exception {
        FichaClinica buscado = new FichaClinica();
        buscado.setNombreMascota("Firulais");

        Mockito.when(fichaClinicaService.findByNombreMascota("Firulais")).thenReturn(java.util.List.of(buscado));

        mockMvc.perform(get("/api/ficha/buscar/nombreMascota/Firulais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreMascota", is("Firulais")));
    }

    @Test
    void testgetFichaClinicaxNombreMascotaVacio() throws Exception {
        Mockito.when(fichaClinicaService.findByNombreMascota("Inexistente")).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/ficha/buscar/nombreMascota/Inexistente"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testgetFichaClinicaxNombreDueño() throws Exception {
        FichaClinica buscado = new FichaClinica();
        buscado.setNombreDueño("Juan Carlos");

        Mockito.when(fichaClinicaService.findByNombreDueño("Juan Carlos")).thenReturn(java.util.List.of(buscado));

        mockMvc.perform(get("/api/ficha/buscar/nombreDueño/Juan Carlos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreDueño", is("Juan Carlos")));
    }

    @Test
    void testgetFichaClinicaxNombreDueñoVacio() throws Exception {
        Mockito.when(fichaClinicaService.findByNombreDueño("Inexistente")).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/ficha/buscar/nombreDueño/Inexistente"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testgetFichaClinicaxRutDueño() throws Exception {
        FichaClinica buscado = new FichaClinica();
        buscado.setRutDueño("12345678-9");

        Mockito.when(fichaClinicaService.findByRutDueño("12345678-9")).thenReturn(java.util.List.of(buscado));

        mockMvc.perform(get("/api/ficha/buscar/rutDueño/12345678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rutDueño", is("12345678-9")));
    }

    @Test
    void testgetFichaClinicaxRutDueñoVacio() throws Exception {
        Mockito.when(fichaClinicaService.findByRutDueño("00000000-0")).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/ficha/buscar/rutDueño/00000000-0"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testpatchFichaClinica() throws Exception {
        FichaClinica actualizado = new FichaClinica();
        actualizado.setIdFicha(1L);
        actualizado.setAlergias("Polen");

        Mockito.when(fichaClinicaService.actualizarFichaClinica(eq(1L), any(FichaClinica.class))).thenReturn(actualizado);

        mockMvc.perform(patch("/api/ficha/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alergias").value("Polen"));
    }

    @Test
    void testpatchFichaClinicaNoExiste() throws Exception {
        FichaClinica datos = new FichaClinica();
        datos.setAlergias("Polen");

        Mockito.when(fichaClinicaService.actualizarFichaClinica(eq(99L), any(FichaClinica.class))).thenReturn(null);

        mockMvc.perform(patch("/api/ficha/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testeliminarFichaClinica() throws Exception {
        Mockito.when(fichaClinicaService.eliminarFichaClinica(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/ficha/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testeliminarFichaClinicaNoExiste() throws Exception {
        Mockito.when(fichaClinicaService.eliminarFichaClinica(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/ficha/99"))
                .andExpect(status().isNotFound());
    }

}