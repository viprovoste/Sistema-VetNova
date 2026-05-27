package com.vetnova.clientes_mascotas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetnova.clientes_mascotas.model.Mascota;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long>{

    List<Mascota> findByNombre(String nombre);
    
}