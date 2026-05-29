package com.vetnova.Clinico.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consulta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConsulta;
 
    @Column(nullable = false)
    @NotBlank(message = "El motivo de la consulta es obligatorio")
    private String motivoConsulta;
 
    @Column(columnDefinition = "VARCHAR(50) DEFAULT 'No se registro'")
    private String temperatura = "No se registro";
 
    @Column(columnDefinition = "VARCHAR(50) DEFAULT 'No se registro'")
    private String pesoActual = "No se registro";
 
    @Column(columnDefinition = "VARCHAR(1000) DEFAULT 'Sin datos'")
    private String observaciones = "Sin datos";
 
    @Column(columnDefinition = "VARCHAR(500) DEFAULT 'Sin diagnostico'")
    private String diagnostico = "Sin diagnostico";
 
    @Column(columnDefinition = "VARCHAR(200) DEFAULT 'Sin receta'")
    private String recetaMedica = "Sin receta";
 
    @Column(columnDefinition = "VARCHAR(500) DEFAULT 'Sin datos'")
    private String indicaciones = "Sin datos";
 
    @Column(columnDefinition = "VARCHAR(500) DEFAULT 'Sin orden de examen'")
    private String ordenExamen = "Sin orden de examen";

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaConsulta;

    @ManyToOne
    @JoinColumn(name = "id_ficha_clinica", nullable = false)
    @JsonBackReference("ficha-consultas")
    private FichaClinica fichaClinica;
    
}
