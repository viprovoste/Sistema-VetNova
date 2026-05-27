package com.vetnova.clientes_mascotas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetnova.clientes_mascotas.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>{

    List<Cliente> findByNombres(String nombres);
    
    List<Cliente> findByRut(String rut);

    List<Cliente> findByNumero(Integer numero);

    List<Cliente> findByCorreo(String correo);

}
