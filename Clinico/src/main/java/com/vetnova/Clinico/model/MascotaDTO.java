package com.vetnova.Clinico.model;
 
import lombok.Data;
 
@Data
public class MascotaDTO {
    private Long idCMascota;
    private String nombre;
    private String especie;
    private String peso;
}