package com.soporte.soporte.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "soportes")
public class Soporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String asunto;
    private String descripcion;
    private String estado;
    private Long usuarioId;

    @OneToMany(mappedBy = "soporte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MensajeSoporte> mensajes;

    public Soporte() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public List<MensajeSoporte> getMensajes() { return mensajes; }
    public void setMensajes(List<MensajeSoporte> mensajes) { this.mensajes = mensajes; }
}