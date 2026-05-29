package com.vetnova.clientes_mascotas.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El rut es obligatorio")
    private String rut;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombres;

    @Column(nullable = false)
    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @Column(nullable = false, unique = true)
    @Email(message = "El correo no tiene formato válido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @Column(nullable = false, unique = true)
    @NotNull(message = "El número es obligatorio")
    private Integer numero;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("mascota")
    @JsonManagedReference
    private List<Mascota> mascotas = new ArrayList<>();
    
}
