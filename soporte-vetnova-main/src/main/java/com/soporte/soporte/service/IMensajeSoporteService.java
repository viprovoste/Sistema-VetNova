package com.soporte.soporte.service;

import com.soporte.soporte.model.MensajeSoporte;

import java.util.List;

public interface IMensajeSoporteService {
    MensajeSoporte agregarMensaje(Long soporteId, MensajeSoporte mensaje);
    List<MensajeSoporte> listarMensajes(Long soporteId);
    void eliminarMensaje(Long id);
}