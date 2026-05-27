package com.vetnova.clientes_mascotas.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(nullable = false)
    @NotBlank(message = "La especie es obligatoria")
    private String especie;

    @Column(nullable = false)
    @NotBlank(message = "El peso es obligatorio")
    private String peso;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false) 
    @JsonProperty("dueño") 
    @com.fasterxml.jackson.annotation.JsonBackReference 
    private Cliente cliente;
    
}
