package com.vetnova.notificaciones.service;

import com.vetnova.notificaciones.model.Notificacion;

import java.util.List;
import java.util.Optional;

public interface INotificacionService {
    Notificacion guardar(Notificacion notificacion);
    List<Notificacion> listarTodas();
    Optional<Notificacion> buscarPorId(Long id);
    void eliminarNotificacion(Long id);
    boolean existeDuplicado(Long idCita, String tipo, String estado);
}