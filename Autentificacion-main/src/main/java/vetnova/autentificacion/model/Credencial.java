package vetnova.autentificacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "credenciales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Referencia al Usuario dueño de la sesión. */
    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false, unique = true, length = 200)
    private String token;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaExpiracion;

    @Builder.Default
    private Boolean activa = true;
}
