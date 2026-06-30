package com.vetnova.Clinico.controller;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.vetnova.Clinico.model.Consulta;
import com.vetnova.Clinico.service.ConsultaService;

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

@WebMvcTest(ConsultaController.class)
@ActiveProfiles("test")
public class ConsultaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultaService consultaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testpostConsulta() throws Exception {
        Consulta consulta = new Consulta();
        consulta.setMotivoConsulta("Control general");

        Consulta guardada = new Consulta();
        guardada.setIdConsulta(1L);
        guardada.setMotivoConsulta("Control general");

        Mockito.when(consultaService.guardarConsulta(eq(1L), any(Consulta.class))).thenReturn(guardada);

        mockMvc.perform(post("/api/consultas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(consulta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.motivoConsulta").value("Control general"));
    }

    @Test
    void testpostConsultaFichaNoExiste() throws Exception {
        Consulta consulta = new Consulta();
        consulta.setMotivoConsulta("Control general");

        Mockito.when(consultaService.guardarConsulta(eq(99L), any(Consulta.class))).thenReturn(null);

        mockMvc.perform(post("/api/consultas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(consulta)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testpostConsultaConflicto() throws Exception {
        Consulta consulta = new Consulta();
        consulta.setMotivoConsulta("Control general");

        Mockito.when(consultaService.guardarConsulta(eq(1L), any(Consulta.class)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/consultas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(consulta)))
                .andExpect(status().isConflict());
    }

    @Test
    void testgetConsultasPorFicha() throws Exception {
        Consulta c1 = new Consulta();
        c1.setIdConsulta(1L);
        c1.setMotivoConsulta("Control general");

        Consulta c2 = new Consulta();
        c2.setIdConsulta(2L);
        c2.setMotivoConsulta("Vacunación");

        Mockito.when(consultaService.listarConsultasPorFicha(1L)).thenReturn(Arrays.asList(c1, c2));

        mockMvc.perform(get("/api/consultas/ficha/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].motivoConsulta", is("Control general")))
                .andExpect(jsonPath("$[1].motivoConsulta", is("Vacunación")));
    }

    @Test
    void testgetConsultasPorFichaVacio() throws Exception {
        Mockito.when(consultaService.listarConsultasPorFicha(1L)).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/consultas/ficha/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testpatchConsulta() throws Exception {
        Consulta actualizado = new Consulta();
        actualizado.setIdConsulta(1L);
        actualizado.setMotivoConsulta("Vacunación");

        Mockito.when(consultaService.actualizarConsulta(eq(1L), any(Consulta.class))).thenReturn(actualizado);

        mockMvc.perform(patch("/api/consultas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivoConsulta").value("Vacunación"));
    }

    @Test
    void testpatchConsultaNoExiste() throws Exception {
        Consulta datos = new Consulta();
        datos.setMotivoConsulta("Vacunación");

        Mockito.when(consultaService.actualizarConsulta(eq(99L), any(Consulta.class))).thenReturn(null);

        mockMvc.perform(patch("/api/consultas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isNotFound());
    }

}