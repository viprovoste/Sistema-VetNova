package com.vetnova.agendamiento_sucursales.model;

import lombok.Data;
 
@Data
public class MascotaDTO {
    private Long idCMascota;
    private String nombre;
    private String especie;
    private String peso;
}
