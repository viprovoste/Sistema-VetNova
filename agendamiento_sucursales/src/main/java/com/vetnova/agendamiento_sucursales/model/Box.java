package com.vetnova.agendamiento_sucursales.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
public class Box {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBox;

    @Column(nullable = false)
    private String numeroBox;       

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoBox estado;        

    @ManyToOne
    @JoinColumn(name = "id_sucursal", nullable = false)
    @JsonBackReference("sucursal-boxes")
    private Sucursal sucursal;
}

