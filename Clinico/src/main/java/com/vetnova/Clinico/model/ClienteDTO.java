package com.vetnova.Clinico.model;

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