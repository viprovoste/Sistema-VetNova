package com.vetnova.Clinico.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FichaClinica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFicha;

    @Column(nullable = false)
    private Long idMascota;

    @Column(nullable = false)
    private Long idCliente;

    @Column(nullable = false)
    private String nombreMascota;

    @Column(nullable = false)
    private String nombreDueño;

    @Column(nullable = false)
    private String apellidoDueño;

    @Column(nullable = false)
    private String rutDueño;

    @Column(nullable = false)
    private String especie;

    @Column(nullable = false)
    private String peso;

    @Column(columnDefinition = "VARCHAR(500) DEFAULT 'Sin alergias'")
    private String alergias = "Sin alergias";

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "fichaClinica", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("ficha-consultas")
    private List<Consulta> consultas = new ArrayList<>();
    
}
