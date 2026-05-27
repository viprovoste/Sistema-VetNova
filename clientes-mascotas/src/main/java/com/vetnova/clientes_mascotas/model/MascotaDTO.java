package com.vetnova.clientes_mascotas.model;

import lombok.Data;

@Data
public class MascotaDTO {
    private String nombre;
    private String especie;
    private String peso;
    private Long idCliente; 
}