package com.vetnova.clientes_mascotas.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MascotaDTO {
    @NotBlank(message = "El nombre de la mascota es obligatorio")
    private String nombre;
    
    @NotBlank(message = "La especie es obligatoria")
    private String especie;
    
    @NotBlank(message = "El peso es obligatorio")
    private String peso;
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long idCliente;
}