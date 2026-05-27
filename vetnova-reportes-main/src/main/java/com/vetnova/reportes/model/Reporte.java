package com.vetnova.reportes.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes_analitica")
public class Reporte {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "El total de atenciones no puede ser nulo")
    @Min(value = 0, message = "El total de atenciones debe ser mayor o igual a 0")
    private Integer totalAtenciones;

    @NotNull(message = "El total de alertas no puede ser nulo")
    @Min(value = 0, message = "El total de alertas debe ser mayor o igual a 0")
    private Integer totalAlertasGeneradas;

    @NotNull(message = "El rendimiento global no puede ser nulo")
    private Double rendimientoGlobal;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Constructor vacío requerido por JPA
    public Reporte() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getTotalAtenciones() { return totalAtenciones; }
    public void setTotalAtenciones(Integer totalAtenciones) { this.totalAtenciones = totalAtenciones; }

    public Integer getTotalAlertasGeneradas() { return totalAlertasGeneradas; }
    public void setTotalAlertasGeneradas(Integer totalAlertasGeneradas) { this.totalAlertasGeneradas = totalAlertasGeneradas; }

    public Double getRendimientoGlobal() { return rendimientoGlobal; }
    public void setRendimientoGlobal(Double rendimientoGlobal) { this.rendimientoGlobal = rendimientoGlobal; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}