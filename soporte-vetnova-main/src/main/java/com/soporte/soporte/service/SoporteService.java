package com.soporte.soporte.service;

import com.soporte.soporte.client.NotificacionClient;
import com.soporte.soporte.client.ReporteClient;
import com.soporte.soporte.dto.SoporteDTO;
import com.soporte.soporte.model.Soporte;
import com.soporte.soporte.repository.SoporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SoporteService {

    private static final Logger logger = LoggerFactory.getLogger(SoporteService.class);

    @Autowired
    private SoporteRepository soporteRepository;

    @Autowired
    private NotificacionClient notificacionClient;

    @Autowired
    private ReporteClient reporteClient;

    public Soporte guardar(Soporte soporte) {
        // 1. Guardar en BD
        Soporte guardado = soporteRepository.save(soporte);
        
        // 2. Mapear a DTO
        SoporteDTO dto = new SoporteDTO();
        dto.setAsunto(guardado.getAsunto());
        dto.setDescripcion(guardado.getDescripcion());
        dto.setEstado(guardado.getEstado());
        dto.setUsuarioId(guardado.getUsuarioId());
        
        // 3. Envío seguro a microservicios
        try {
            notificacionClient.enviarNotificacion(dto);
        } catch (Exception e) {
            logger.error("Error al notificar: {}", e.getMessage());
        }

        try {
            reporteClient.registrarDato(dto);
        } catch (Exception e) {
            logger.error("Error al registrar reporte: {}", e.getMessage());
        }
        
        return guardado;
    }
}