package com.vetnova.notificaciones.service;

import com.vetnova.notificaciones.model.Notificacion;
import com.vetnova.notificaciones.repository.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificacionService implements INotificacionService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionService.class);
    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Override
    public Notificacion guardar(Notificacion notificacion) {
        logger.info("LOG: Procesando guardado de notificacion para destino: {}", notificacion.getDestinatario());
        Notificacion guardada = notificacionRepository.save(notificacion);
        logger.info("LOG: Notificacion guardada con ID: {}", guardada.getId());
        return guardada;
    }

    @Override
    public List<Notificacion> listarTodas() {
        logger.info("LOG: Consultando historial de notificaciones.");
        return notificacionRepository.findAll();
    }

    @Override
    public Optional<Notificacion> buscarPorId(Long id) {
        logger.info("LOG: Buscando notificacion con ID: {}", id);
        return notificacionRepository.findById(id);
    }

    @Override
    public void eliminarNotificacion(Long id) {
        logger.info("LOG: Eliminando notificacion con ID: {}", id);
        if (!notificacionRepository.existsById(id)) {
            throw new RuntimeException("Notificación no encontrada con ID: " + id);
        }
        notificacionRepository.deleteById(id);
    }

    @Override
    public boolean existeDuplicado(Long idCita, String tipo, String estado) {
        return notificacionRepository.existsByIdCitaAndTipoAndEstado(idCita, tipo, estado);
    }
}