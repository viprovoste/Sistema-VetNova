package com.vetnova.agendamiento_sucursales.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoraMedica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHoraMedica;

    @Column(nullable = false)
    private Long idMascota;

    @Column(nullable = false)
    private Long idCliente;

    @Column(nullable = false)
    private String nombreMascota;

    @Column(nullable = false)
    private String especie;

    @Column(nullable = false)
    private String nombreDueño;

    @Column(nullable = false)
    private String apellidoDueño;

    @Column(nullable = false)
    private String rutDueño;

    @Column(nullable = false)
    private String motivoConsulta;

    @Column(nullable = false)
    private LocalDateTime fechaHora;         

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoHora estado;              

    @Column(nullable = false)
    private String nombreVeterinario;

    @ManyToOne
    @JoinColumn(name = "id_box", nullable = false)
    private Box box;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaCreacion;
}
