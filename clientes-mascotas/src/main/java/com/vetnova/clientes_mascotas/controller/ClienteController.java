package com.vetnova.clientes_mascotas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.vetnova.clientes_mascotas.model.Cliente;
import com.vetnova.clientes_mascotas.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> getClientes() {
        List<Cliente> clientes = clienteService.listarClientes();
        if (clientes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Cliente> postCliente(@Valid @RequestBody Cliente cliente) {
        Cliente nuevo;
        try {
            nuevo = clienteService.guardarCliente(cliente);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClientexId(@PathVariable Long id) {
        Cliente buscado=clienteService.findById(id).orElse(null);
        if(buscado==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscado,HttpStatus.OK);
    }

    @GetMapping("/buscar/nombres/{nombres}")
    public ResponseEntity<List<Cliente>> getClientexNombres(@PathVariable String nombres) {
        List<Cliente> buscados = clienteService.findByNombres(nombres);
        if(buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados,HttpStatus.OK);
    }

    @GetMapping("/buscar/rut/{rut}")
    public ResponseEntity<List<Cliente>> getClientexRut(@PathVariable String rut) {
        List<Cliente> buscados = clienteService.findByRut(rut);
        if(buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados,HttpStatus.OK);
    }

    @GetMapping("/buscar/numero/{numero}")
    public ResponseEntity<List<Cliente>> getClientexNumero(@PathVariable Integer numero) {
        List<Cliente> buscados = clienteService.findByNumero(numero);
        if(buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados,HttpStatus.OK);
    }

    @GetMapping("/buscar/correo/{correo}")
    public ResponseEntity<List<Cliente>> getClientexCorreo(@PathVariable String correo) {
        List<Cliente> buscados = clienteService.findByCorreo(correo);
        if(buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados,HttpStatus.OK);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<Cliente> patchCliente(@PathVariable Long id, @RequestBody Cliente datosaCambiar) {
        Cliente actualizado = clienteService.actualizarCliente(id, datosaCambiar);
        
        if (actualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        boolean eliminado = clienteService.eliminarCliente(id);
        if (!eliminado) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
