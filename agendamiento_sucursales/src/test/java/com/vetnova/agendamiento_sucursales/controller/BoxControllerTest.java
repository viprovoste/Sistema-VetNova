package com.vetnova.agendamiento_sucursales.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.vetnova.agendamiento_sucursales.model.Box;
import com.vetnova.agendamiento_sucursales.model.EstadoBox;
import com.vetnova.agendamiento_sucursales.model.Sucursal;
import com.vetnova.agendamiento_sucursales.service.BoxService;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@WebMvcTest(BoxController.class)
@ActiveProfiles("test")
public class BoxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BoxService boxService;


    private Box crearBox() {
        Sucursal sucursal = new Sucursal();
        sucursal.setIdSucursal(1L);
        return new Box(1L, "Box-1", EstadoBox.DISPONIBLE, sucursal);
    }

    @Test
    void testgetBoxesPorSucursal() throws Exception {
        Mockito.when(boxService.listarBoxesPorSucursal(1L)).thenReturn(java.util.List.of(crearBox()));

        mockMvc.perform(get("/api/boxes/sucursal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numeroBox", is("Box-1")))
                .andExpect(jsonPath("$[0].estado", is("DISPONIBLE")));
    }

    @Test
    void testgetBoxesPorSucursalVacio() throws Exception {
        Mockito.when(boxService.listarBoxesPorSucursal(1L)).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/boxes/sucursal/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testgetBoxesPorEstado() throws Exception {
        Mockito.when(boxService.listarBoxesPorEstado(1L, EstadoBox.DISPONIBLE)).thenReturn(java.util.List.of(crearBox()));

        mockMvc.perform(get("/api/boxes/sucursal/1/estado")
                        .param("estado", "DISPONIBLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].estado", is("DISPONIBLE")));
    }

    @Test
    void testgetBoxesPorEstadoVacio() throws Exception {
        Mockito.when(boxService.listarBoxesPorEstado(1L, EstadoBox.OCUPADO)).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/boxes/sucursal/1/estado")
                        .param("estado", "OCUPADO"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testpatchEstadoBox() throws Exception {
        Box actualizado = crearBox();
        actualizado.setEstado(EstadoBox.OCUPADO);

        Mockito.when(boxService.actualizarEstadoBox(eq(1L), eq(EstadoBox.OCUPADO))).thenReturn(actualizado);

        mockMvc.perform(patch("/api/boxes/1")
                        .param("estado", "OCUPADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("OCUPADO")));
    }

    @Test
    void testpatchEstadoBoxNoExiste() throws Exception {
        Mockito.when(boxService.actualizarEstadoBox(eq(99L), eq(EstadoBox.OCUPADO))).thenReturn(null);

        mockMvc.perform(patch("/api/boxes/99")
                        .param("estado", "OCUPADO"))
                .andExpect(status().isNotFound());
    }

}
