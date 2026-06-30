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
import com.vetnova.clientes_mascotas.service.ClienteService;

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

@WebMvcTest(ClienteController.class)
@ActiveProfiles("test")
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testgetClientes() throws Exception {
        Cliente m1 = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);
        Cliente m2 = new Cliente(2L, "23456789-0", "María Fernanda", "Soto Ramírez", "maria.soto@email.com", 923456789, null, null);

        Mockito.when(clienteService.listarClientes()).thenReturn(Arrays.asList(m1, m2));

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombres", is("Juan Carlos")))
                .andExpect(jsonPath("$[0].rut", is("12345678-9")))
                .andExpect(jsonPath("$[1].apellidos", is("Soto Ramírez")))
                .andExpect(jsonPath("$[1].correo", is("maria.soto@email.com")));
    } 

    @Test
    void testgetClientesVacio() throws Exception {
        Mockito.when(clienteService.listarClientes()).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testpostCliente() throws Exception {
        Cliente nuevo = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);
        Cliente guardado = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);

        Mockito.when(clienteService.guardarCliente(any(Cliente.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombres").value("Juan Carlos"))
                .andExpect(jsonPath("$.rut").value("12345678-9"))
                .andExpect(jsonPath("$.apellidos").value("Pérez González"))
                .andExpect(jsonPath("$.correo").value("juan.perez@email.com"));
    }

    @Test
    void testpostClienteConflicto() throws Exception {
        Cliente nuevo = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);

        Mockito.when(clienteService.guardarCliente(any(Cliente.class)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isConflict());
    }

    @Test
    void testgetClientexId() throws Exception {
        Cliente buscado = new Cliente(2L, "23456789-0", "María Fernanda", "Soto Ramírez", "maria.soto@email.com", 923456789, null, null);

        Mockito.when(clienteService.findById(2L)).thenReturn(Optional.of(buscado));
        
        mockMvc.perform(get("/api/clientes/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(2L))
                .andExpect(jsonPath("$.nombres").value("María Fernanda"));

    }

    @Test
    void testgetClientexIdNoExistente() throws Exception {
        Mockito.when(clienteService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testgetClientexNombres() throws Exception {
        Cliente buscado = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);

        Mockito.when(clienteService.findByNombres("Juan Carlos")).thenReturn(java.util.List.of(buscado));

        mockMvc.perform(get("/api/clientes/buscar/nombres/Juan Carlos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombres", is("Juan Carlos")));
    }

    @Test
    void testgetClientexNombresVacio() throws Exception {
        Mockito.when(clienteService.findByNombres("Inexistente")).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/clientes/buscar/nombres/Inexistente"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testgetClientexRut() throws Exception {
        Cliente buscado = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);

        Mockito.when(clienteService.findByRut("12345678-9")).thenReturn(java.util.List.of(buscado));

        mockMvc.perform(get("/api/clientes/buscar/rut/12345678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rut", is("12345678-9")));
    }

    @Test
    void testgetClientexRutVacio() throws Exception {
        Mockito.when(clienteService.findByRut("00000000-0")).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/clientes/buscar/rut/00000000-0"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testgetClientexNumero() throws Exception {
        Cliente buscado = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);

        Mockito.when(clienteService.findByNumero(912345678)).thenReturn(java.util.List.of(buscado));

        mockMvc.perform(get("/api/clientes/buscar/numero/912345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numero", is(912345678)));
    }

    @Test
    void testgetClientexNumeroVacio() throws Exception {
        Mockito.when(clienteService.findByNumero(0)).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/clientes/buscar/numero/0"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testgetClientexCorreo() throws Exception {
        Cliente buscado = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);

        Mockito.when(clienteService.findByCorreo("juan.perez@email.com")).thenReturn(java.util.List.of(buscado));

        mockMvc.perform(get("/api/clientes/buscar/correo/juan.perez@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correo", is("juan.perez@email.com")));
    }

    @Test
    void testgetClientexCorreoVacio() throws Exception {
        Mockito.when(clienteService.findByCorreo("nadie@email.com")).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/clientes/buscar/correo/nadie@email.com"))
                .andExpect(status().isNoContent());
    }


    @Test
    void testpatchCliente() throws Exception {
        Cliente actualizado = new Cliente(1L, "12345678-9", "Tulio Triviño", "Pérez González", "tulio@email.com", 912345678, null, null);

        Mockito.when(clienteService.actualizarCliente(Mockito.eq(1L), any(Cliente.class))).thenReturn(actualizado);

        mockMvc.perform(patch("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres").value("Tulio Triviño"));
    }

    @Test
    void testpatchClienteNoExiste() throws Exception {
        Cliente datos = new Cliente(null, null, "Tulio", null, null, null, null, null);

        Mockito.when(clienteService.actualizarCliente(Mockito.eq(99L), any(Cliente.class))).thenReturn(null);

        mockMvc.perform(patch("/api/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testeliminarCliente() throws Exception {
        Mockito.when(clienteService.eliminarCliente(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());
    }
    
}
