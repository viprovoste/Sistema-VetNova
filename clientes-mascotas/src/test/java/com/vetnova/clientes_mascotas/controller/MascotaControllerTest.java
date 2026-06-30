package com.vetnova.clientes_mascotas.controller;

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

import com.vetnova.clientes_mascotas.model.Cliente;
import com.vetnova.clientes_mascotas.model.Mascota;
import com.vetnova.clientes_mascotas.model.MascotaDTO;
import com.vetnova.clientes_mascotas.service.MascotaService;

import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@WebMvcTest(MascotaController.class)
@ActiveProfiles("test")
public class MascotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MascotaService mascotaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testgetMascotas() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);

        Mascota m1 = new Mascota(1L, "Firulais", "Perro", "20", null, cliente);
        Mascota m2 = new Mascota(2L, "Pelusa", "Gato", "8", null, cliente);

        Mockito.when(mascotaService.listarMascotas()).thenReturn(Arrays.asList(m1, m2));

        mockMvc.perform(get("/api/mascotas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Firulais")))
                .andExpect(jsonPath("$[0].especie", is("Perro")))
                .andExpect(jsonPath("$[1].nombre", is("Pelusa")))
                .andExpect(jsonPath("$[1].peso", is("8")));
    }

    @Test
    void testgetMascotasVacio() throws Exception {
        Mockito.when(mascotaService.listarMascotas()).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/mascotas"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testpostMascota() throws Exception {
        MascotaDTO dto = new MascotaDTO();
        dto.setNombre("Firulais");
        dto.setEspecie("Perro");
        dto.setPeso("20");
        dto.setIdCliente(1L);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);

        Mascota guardada = new Mascota(1L, "Firulais", "Perro", "20", null, cliente);

        Mockito.when(mascotaService.guardarMascota(any(MascotaDTO.class))).thenReturn(guardada);

        mockMvc.perform(post("/api/mascotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Firulais"))
                .andExpect(jsonPath("$.especie").value("Perro"))
                .andExpect(jsonPath("$.peso").value("20"));
    }

    @Test
    void testpostMascotaBadRequest() throws Exception {
        MascotaDTO dto = new MascotaDTO();
        dto.setNombre("Firulais");
        dto.setEspecie("Perro");
        dto.setPeso("20");
        dto.setIdCliente(99L);

        Mockito.when(mascotaService.guardarMascota(any(MascotaDTO.class)))
                .thenThrow(new RuntimeException("Cliente no encontrado"));

        mockMvc.perform(post("/api/mascotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testgetMascotaxId() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);

        Mascota buscado = new Mascota(1L, "Firulais", "Perro", "20", null, cliente);

        Mockito.when(mascotaService.findById(1L)).thenReturn(Optional.of(buscado));

        mockMvc.perform(get("/api/mascotas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Firulais"));
    }

    @Test
    void testgetMascotaxIdNoExistente() throws Exception {
        Mockito.when(mascotaService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/mascotas/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testgetMascotaxNombre() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);

        Mascota buscado = new Mascota(1L, "Firulais", "Perro", "20", null, cliente);

        Mockito.when(mascotaService.findByNombre("Firulais")).thenReturn(java.util.List.of(buscado));

        mockMvc.perform(get("/api/mascotas/buscar/nombre/Firulais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Firulais")));
    }

    @Test
    void testgetMascotaxNombreVacio() throws Exception {
        Mockito.when(mascotaService.findByNombre("Inexistente")).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/mascotas/buscar/nombre/Inexistente"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testpatchMascota() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);

        Mascota actualizado = new Mascota(1L, "Pelusa", "Gato", "8", null, cliente);

        Mockito.when(mascotaService.actualizarMascota(Mockito.eq(1L), any(Mascota.class))).thenReturn(actualizado);

        mockMvc.perform(patch("/api/mascotas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pelusa"));
    }

    @Test
    void testpatchMascotaNoExiste() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);

        Mascota datos = new Mascota(99L, "Pelusa", "Gato", "8", null, cliente);

        Mockito.when(mascotaService.actualizarMascota(Mockito.eq(99L), any(Mascota.class))).thenReturn(null);

        mockMvc.perform(patch("/api/mascotas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testeliminarMascota() throws Exception {
        Mockito.when(mascotaService.eliminarMascota(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/mascotas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testeliminarMascotaNoExiste() throws Exception {
        Mockito.when(mascotaService.eliminarMascota(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/mascotas/99"))
                .andExpect(status().isNotFound());
    }

}
