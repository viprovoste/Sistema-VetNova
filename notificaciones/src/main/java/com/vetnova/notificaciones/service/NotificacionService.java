package com.vetnova.notificaciones.service;

import com.vetnova.notificaciones.model.Notificacion;
import com.vetnova.notificaciones.repository.NotificacionRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

@Service
public class NotificacionService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionService.class);
    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public Notificacion guardar(Notificacion notificacion) {
        logger.info("LOG: Procesando guardado de notificacion para destino: {}", notificacion.getDestinatario());
        Notificacion guardada = notificacionRepository.save(notificacion);
        logger.info("LOG: Notificacion guardada con ID: {}", guardada.getId());
        return guardada;
    }

    public List<Notificacion> listarTodas() {
        logger.info("LOG: Consultando historial de notificaciones.");
        return notificacionRepository.findAll();
    }

    public Optional<Notificacion> buscarPorId(Long id) {
        logger.info("LOG: Buscando notificacion con ID: {}", id);
        return notificacionRepository.findById(id);
    }

    public void eliminarNotificacion(Long id) {
        logger.info("LOG: Eliminando notificacion con ID: {}", id);
        notificacionRepository.deleteById(id);
    }
}