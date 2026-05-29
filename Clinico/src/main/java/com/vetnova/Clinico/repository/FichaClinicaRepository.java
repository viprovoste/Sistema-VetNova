package com.vetnova.Clinico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetnova.Clinico.model.FichaClinica;

@Repository
public interface FichaClinicaRepository  extends JpaRepository<FichaClinica, Long>{

    List<FichaClinica> findByNombreMascota(String nombreMascota);

    List<FichaClinica> findByNombreDueño(String nombreDueño);

    List<FichaClinica> findByRutDueño(String rutDueño);
    
}
