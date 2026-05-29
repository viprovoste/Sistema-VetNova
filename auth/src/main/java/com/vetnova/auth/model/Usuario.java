package com.vetnova.auth.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Puede ser el correo o un nombre de usuario

    @Column(nullable = false)
    private String password; // ¡Aquí guardaremos la clave ENCRIPTADA!

    private String nombre;
    private String apellido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
}
