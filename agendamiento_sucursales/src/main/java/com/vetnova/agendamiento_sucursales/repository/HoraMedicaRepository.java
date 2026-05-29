package com.vetnova.agendamiento_sucursales.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetnova.agendamiento_sucursales.model.HoraMedica;

@Repository
public interface HoraMedicaRepository extends JpaRepository<HoraMedica, Long>{

    List<HoraMedica> findByNombreMascota(String nombreMascota);

    List<HoraMedica> findByNombreDueño(String nombreDueño);

    List<HoraMedica> findByRutDueño(String rutDueño);
    
    List<HoraMedica> findByNombreVeterinario(String nombreVeterinario);
    
}
