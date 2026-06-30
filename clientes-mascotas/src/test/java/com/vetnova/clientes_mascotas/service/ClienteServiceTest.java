package com.vetnova.clientes_mascotas.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vetnova.clientes_mascotas.model.Cliente;
import com.vetnova.clientes_mascotas.repository.ClienteRepository;

public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testguardarCliente() {
        Cliente cliente = new Cliente(null, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);
        Cliente clienteGuardado = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);

        when(clienteRepository.save(cliente)).thenReturn(clienteGuardado);

        Cliente resultado = clienteService.guardarCliente(cliente);

        assertThat(resultado).isEqualTo(clienteGuardado);
        assertThat(resultado.getIdCliente()).isEqualTo(1L);
        assertThat(resultado.getNombres()).isEqualTo("Juan Carlos");

        verify(clienteRepository).save(cliente);

    }

    @Test
    void testListarCliente() {
        Cliente cliente = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);
        List<Cliente> clientes = new ArrayList<>();
        clientes.add(cliente);

        when(clienteRepository.findAll()).thenReturn(clientes);

        List<Cliente> resultado = clienteService.listarClientes();

        assertThat(resultado).hasSize(1);
        assertThat(resultado).contains(cliente);

        verify(clienteRepository).findAll();        
    }

    @Test
    void testfindById() {
        Cliente cliente = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = clienteService.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombres()).isEqualTo("Juan Carlos");

        verify(clienteRepository).findById(1L);
    }

    @Test
    void testfindByNombres() {
        Cliente cliente = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);

        when(clienteRepository.findByNombres("Juan Carlos")).thenReturn(List.of(cliente));

        List<Cliente> resultado = clienteService.findByNombres("Juan Carlos");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombres()).isEqualTo("Juan Carlos");

        verify(clienteRepository).findByNombres("Juan Carlos");
    }

    @Test
    void testfindByRut() {
        Cliente cliente = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);

        when(clienteRepository.findByRut("12345678-9")).thenReturn(List.of(cliente));

        List<Cliente> resultado = clienteService.findByRut("12345678-9");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRut()).isEqualTo("12345678-9");

        verify(clienteRepository).findByRut("12345678-9");
    }

    @Test
    void testfindByNumero() {
        Cliente cliente = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);

        when(clienteRepository.findByNumero(912345678)).thenReturn(List.of(cliente));

        List<Cliente> resultado = clienteService.findByNumero(912345678);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNumero()).isEqualTo(912345678);

        verify(clienteRepository).findByNumero(912345678);
    }

    @Test
    void testfindByCorreo() {
        Cliente cliente = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);

        when(clienteRepository.findByCorreo("juan.perez@email.com")).thenReturn(List.of(cliente));

        List<Cliente> resultado = clienteService.findByCorreo("juan.perez@email.com");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCorreo()).isEqualTo("juan.perez@email.com");

        verify(clienteRepository).findByCorreo("juan.perez@email.com");
    }

    @Test
    void testactualizarCliente() {
        Cliente clienteExistente = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);
        Cliente clienteActualizado = new Cliente(null, "12345678-9", "Tulio Triviño", "Pérez González", "tulio.triviño@email.com", 912345678, null, null);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Cliente resultado = clienteService.actualizarCliente(1L, clienteActualizado);

        assertThat(resultado.getRut()).isEqualTo("12345678-9");
        assertThat(resultado.getNombres()).isEqualTo("Tulio Triviño");
        assertThat(resultado.getApellidos()).isEqualTo("Pérez González");
        assertThat(resultado.getCorreo()).isEqualTo("tulio.triviño@email.com");
        assertThat(resultado.getNumero()).isEqualTo(912345678);

        verify(clienteRepository).findById(1L);
        verify(clienteRepository).save(clienteExistente);
    }

    @Test
    void testactualizarClienteNoExiste() {
        Cliente clienteActualizado = new Cliente(null, "12345678-9", "Tulio Triviño", "Pérez González", "tulio.triviño@email.com", 912345678, null, null);

        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        Cliente resultado = clienteService.actualizarCliente(1L, clienteActualizado);

        assertThat(resultado).isNull();

        verify(clienteRepository).findById(1L);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void testactualizarClienteCamposNulos() {
        Cliente clienteExistente = new Cliente(1L, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);
        Cliente datosVacios = new Cliente(null, null, null, null, null, null, null, null);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Cliente resultado = clienteService.actualizarCliente(1L, datosVacios);

        assertThat(resultado.getRut()).isEqualTo("12345678-9");
        assertThat(resultado.getNombres()).isEqualTo("Juan Carlos");
        assertThat(resultado.getApellidos()).isEqualTo("Pérez González");
        assertThat(resultado.getCorreo()).isEqualTo("juan.perez@email.com");
        assertThat(resultado.getNumero()).isEqualTo(912345678);

        verify(clienteRepository).findById(1L);
        verify(clienteRepository).save(clienteExistente);
    }
    
    @Test
    void testeliminarCliente() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(1L);

        clienteService.eliminarCliente(1L);

        verify(clienteRepository).deleteById(1L);
    }

    @Test
    void testeliminarClienteNoExiste() {
        when(clienteRepository.existsById(1L)).thenReturn(false);

        boolean resultado = clienteService.eliminarCliente(1L);

        assertThat(resultado).isFalse();

        verify(clienteRepository).existsById(1L);
        verify(clienteRepository, never()).deleteById(1L);
    }

}