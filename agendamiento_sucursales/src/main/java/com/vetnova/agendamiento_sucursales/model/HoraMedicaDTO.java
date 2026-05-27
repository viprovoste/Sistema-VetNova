package com.vetnova.agendamiento_sucursales.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class HoraMedicaDTO {
    private Long idMascota;
    private Long idCliente;
    private String motivoConsulta;
    private LocalDateTime fechaHora;
    private String nombreVeterinario;
    private Long idBox;

}
