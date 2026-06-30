package com.vetnova.clientes_mascotas.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import com.vetnova.clientes_mascotas.model.Cliente;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void testGuardarYBuscarCliente() {
        Cliente cliente = new Cliente(null, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);
        Cliente guardado = clienteRepository.save(cliente);

        Optional<Cliente> encontrado = clienteRepository.findById(guardado.getIdCliente());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getRut()).isEqualTo("12345678-9");
        assertThat(encontrado.get().getNombres()).isEqualTo("Juan Carlos");
        assertThat(encontrado.get().getApellidos()).isEqualTo("Pérez González");
        assertThat(encontrado.get().getCorreo()).isEqualTo("juan.perez@email.com");
        assertThat(encontrado.get().getNumero()).isEqualTo(912345678);
    }
    
    @Test
    void testEliminarCliente() {
        Cliente cliente = new Cliente(null, "12345678-9", "Juan Carlos", "Pérez González", "juan.perez@email.com", 912345678, null, null);
        Cliente guardado = clienteRepository.save(cliente);

        clienteRepository.deleteById(guardado.getIdCliente());
        Optional<Cliente> encontrado = clienteRepository.findById(guardado.getIdCliente());

        assertThat(encontrado).isNotPresent();
    }

    @Test
    void testFindByNombres() {
        Cliente cliente = new Cliente();
        cliente.setRut("12345678-9");
        cliente.setNombres("Juan Carlos");
        cliente.setApellidos("Pérez González");
        cliente.setCorreo("juan.perez@correo.com");
        cliente.setNumero(912345678);
        clienteRepository.save(cliente);

        List<Cliente> encontrados = clienteRepository.findByNombres("Juan Carlos");

        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).getNombres()).isEqualTo("Juan Carlos");
    }

    @Test
    void testfindByRut() {
        Cliente cliente = new Cliente();
        cliente.setRut("12345678-9");
        cliente.setNombres("Juan Carlos");
        cliente.setApellidos("Pérez González");
        cliente.setCorreo("juan.perez@correo.com");
        cliente.setNumero(912345678);
        clienteRepository.save(cliente);

        List<Cliente> encontrados = clienteRepository.findByRut("12345678-9");

        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).getRut()).isEqualTo("12345678-9");

    }

    @Test
    void testfindByNumero() {
        Cliente cliente = new Cliente();
        cliente.setRut("12345678-9");
        cliente.setNombres("Juan Carlos");
        cliente.setApellidos("Pérez González");
        cliente.setCorreo("juan.perez@correo.com");
        cliente.setNumero(912345678);
        clienteRepository.save(cliente);

        List<Cliente> encontrados = clienteRepository.findByNumero(912345678);

        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).getNumero()).isEqualTo(912345678);

    }

    @Test
    void testfindByCorreo() {
        Cliente cliente = new Cliente();
        cliente.setRut("12345678-9");
        cliente.setNombres("Juan Carlos");
        cliente.setApellidos("Pérez González");
        cliente.setCorreo("juan.perez@correo.com");
        cliente.setNumero(912345678);
        clienteRepository.save(cliente);

        List<Cliente> encontrados = clienteRepository.findByCorreo("juan.perez@correo.com");

        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).getCorreo()).isEqualTo("juan.perez@correo.com");

    }

}
