package com.soporte.soporte.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes_soporte")
public class MensajeSoporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El contenido del mensaje no puede estar vacío")
    private String contenido;

    private String autor;

    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "soporte_id")
    @JsonIgnore
    private Soporte soporte;

    public MensajeSoporte() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public Soporte getSoporte() { return soporte; }
    public void setSoporte(Soporte soporte) { this.soporte = soporte; }
}