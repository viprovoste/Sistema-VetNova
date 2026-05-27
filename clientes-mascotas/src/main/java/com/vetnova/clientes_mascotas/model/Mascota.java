package com.vetnova.clientes_mascotas.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Mascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCMascota;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String especie;

    @Column(nullable = false)
    private String peso;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false) 
    @JsonProperty("dueño") 
    @com.fasterxml.jackson.annotation.JsonBackReference 
    private Cliente cliente;
    
}
