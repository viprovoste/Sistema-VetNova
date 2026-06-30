package com.vetnova.agendamiento_sucursales.controller;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.vetnova.agendamiento_sucursales.model.Sucursal;
import com.vetnova.agendamiento_sucursales.service.SucursalService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(SucursalController.class)
@ActiveProfiles("test")
public class SucursalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SucursalService sucursalService;

    @Test
    void testgetSucursales() throws Exception {
        Sucursal s1 = new Sucursal();
        s1.setIdSucursal(1L);
        s1.setNombre("VetNova Centro");

        Sucursal s2 = new Sucursal();
        s2.setIdSucursal(2L);
        s2.setNombre("VetNova Sur");

        Mockito.when(sucursalService.listarSucursales()).thenReturn(Arrays.asList(s1, s2));

        mockMvc.perform(get("/api/sucursales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("VetNova Centro")))
                .andExpect(jsonPath("$[1].nombre", is("VetNova Sur")));
    }

    @Test
    void testgetSucursalesVacio() throws Exception {
        Mockito.when(sucursalService.listarSucursales()).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/sucursales"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testgetSucursalxId() throws Exception {
        Sucursal buscado = new Sucursal();
        buscado.setIdSucursal(1L);
        buscado.setNombre("VetNova Centro");

        Mockito.when(sucursalService.findById(1L)).thenReturn(Optional.of(buscado));

        mockMvc.perform(get("/api/sucursales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("VetNova Centro"));
    }

    @Test
    void testgetSucursalxIdNoExistente() throws Exception {
        Mockito.when(sucursalService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/sucursales/99"))
                .andExpect(status().isNoContent());
    }

}