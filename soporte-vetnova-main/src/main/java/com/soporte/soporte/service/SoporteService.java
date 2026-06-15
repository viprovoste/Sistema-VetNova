package com.soporte.soporte.service;

import com.soporte.soporte.client.NotificacionClient;
import com.soporte.soporte.client.ReporteClient;
import com.soporte.soporte.dto.SoporteDTO;
import com.soporte.soporte.model.Soporte;
import com.soporte.soporte.repository.SoporteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SoporteService implements ISoporteService {

    private static final Logger logger = LoggerFactory.getLogger(SoporteService.class);

    private final SoporteRepository soporteRepository;
    private final NotificacionClient notificacionClient;
    private final ReporteClient reporteClient;

    public SoporteService(SoporteRepository soporteRepository,
                          NotificacionClient notificacionClient,
                          ReporteClient reporteClient) {
        this.soporteRepository = soporteRepository;
        this.notificacionClient = notificacionClient;
        this.reporteClient = reporteClient;
    }

    @Override
    public Soporte guardar(Soporte soporte) {
        Soporte guardado = soporteRepository.save(soporte);

        SoporteDTO dto = new SoporteDTO();
        dto.setAsunto(guardado.getAsunto());
        dto.setDescripcion(guardado.getDescripcion());
        dto.setEstado(guardado.getEstado());
        dto.setUsuarioId(guardado.getUsuarioId());

        try {
            notificacionClient.enviarNotificacion(dto);
            logger.info("Notificacion enviada correctamente");
        } catch (Exception e) {
            logger.error("Error al notificar: {}", e.getMessage());
        }

        try {
            reporteClient.registrarDato(dto);
            logger.info("Reporte registrado correctamente");
        } catch (Exception e) {
            logger.error("Error al registrar reporte: {}", e.getMessage());
        }

        return guardado;
    }

    @Override
    public List<Soporte> listarTodos() {
        logger.info("LOG: Listando todos los soportes");
        return soporteRepository.findAll();
    }

    @Override
    public Optional<Soporte> buscarPorId(Long id) {
        logger.info("LOG: Buscando soporte con ID: {}", id);
        return soporteRepository.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        logger.info("LOG: Eliminando soporte con ID: {}", id);
        if (!soporteRepository.existsById(id)) {
            throw new RuntimeException("Soporte no encontrado con ID: " + id);
        }
        soporteRepository.deleteById(id);
    }

    @Override
    public boolean existeDuplicado(Long usuarioId, String asunto) {
        return soporteRepository.existsByUsuarioIdAndAsuntoAndEstadoNot(usuarioId, asunto, "RESUELTO");
    }
}