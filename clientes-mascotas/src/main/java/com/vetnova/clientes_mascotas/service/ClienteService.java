package com.vetnova.clientes_mascotas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetnova.clientes_mascotas.model.Cliente;
import com.vetnova.clientes_mascotas.repository.ClienteRepository;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarClientes() {
    return clienteRepository.findByActivoTrue();
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public List<Cliente> findByNombres(String nombres) {
        return clienteRepository.findByNombres(nombres);
    }

    public List<Cliente> findByRut(String rut) {
        return clienteRepository.findByRut(rut);
    }

    public List<Cliente> findByNumero(Integer numero) {
        return clienteRepository.findByNumero(numero);
    }

    public List<Cliente> findByCorreo(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

    public Cliente actualizarCliente(Long id, Cliente datosaCambiar) {
        Optional<Cliente> clienteExistente = clienteRepository.findById(id);

        if (clienteExistente.isPresent()) {
            Cliente cliente = clienteExistente.get();

            if (datosaCambiar.getNombres() != null) {
                cliente.setNombres(datosaCambiar.getNombres());
            }
            if (datosaCambiar.getApellidos() != null) {
                cliente.setApellidos(datosaCambiar.getApellidos());
            }
            if (datosaCambiar.getCorreo() != null) {
                cliente.setCorreo(datosaCambiar.getCorreo());
            }
            if (datosaCambiar.getRut() != null) {
                cliente.setRut(datosaCambiar.getRut());
            }            
            if (datosaCambiar.getNumero() != null) { 
                cliente.setNumero(datosaCambiar.getNumero());
            }
            return clienteRepository.save(cliente);
        }        
        return null; 
    }

    public Cliente desactivarCliente(Long id) {
    Optional<Cliente> clienteExistente = clienteRepository.findById(id);
        if (clienteExistente.isPresent()) {
            Cliente cliente = clienteExistente.get();
            cliente.setActivo(false);
            return clienteRepository.save(cliente);
        }
        return null;
    }
    
}
