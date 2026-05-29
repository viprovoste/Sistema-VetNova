package com.soporte.soporte.service;

import com.soporte.soporte.model.MensajeSoporte;
import com.soporte.soporte.model.Soporte;
import com.soporte.soporte.repository.MensajeSoporteRepository;
import com.soporte.soporte.repository.SoporteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensajeSoporteService {

    private static final Logger logger = LoggerFactory.getLogger(MensajeSoporteService.class);

    private final MensajeSoporteRepository mensajeRepository;
    private final SoporteRepository soporteRepository;

    public MensajeSoporteService(MensajeSoporteRepository mensajeRepository, SoporteRepository soporteRepository) {
        this.mensajeRepository = mensajeRepository;
        this.soporteRepository = soporteRepository;
    }

    public MensajeSoporte agregarMensaje(Long soporteId, MensajeSoporte mensaje) {
        logger.info("LOG: Agregando mensaje al soporte ID: {}", soporteId);
        Soporte soporte = soporteRepository.findById(soporteId)
                .orElseThrow(() -> new RuntimeException("Soporte no encontrado con ID: " + soporteId));
        mensaje.setSoporte(soporte);
        return mensajeRepository.save(mensaje);
    }

    public List<MensajeSoporte> listarMensajes(Long soporteId) {
        logger.info("LOG: Listando mensajes del soporte ID: {}", soporteId);
        return mensajeRepository.findBySoporteId(soporteId);
    }

    public void eliminarMensaje(Long id) {
        logger.info("LOG: Eliminando mensaje ID: {}", id);
        mensajeRepository.deleteById(id);
    }
}