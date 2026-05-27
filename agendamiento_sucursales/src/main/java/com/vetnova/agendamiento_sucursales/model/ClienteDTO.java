package com.vetnova.agendamiento_sucursales.model;

import lombok.Data;

@Data
public class ClienteDTO {
    private Long idCliente;
    private String nombres;
    private String apellidos;
    private String rut;
    private String correo;
    private int numero;
}
