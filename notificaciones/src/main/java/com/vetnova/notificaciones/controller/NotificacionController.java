package com.vetnova.notificaciones.controller;

import com.vetnova.notificaciones.dto.SoporteDTO;
import com.vetnova.notificaciones.model.Notificacion;
import com.vetnova.notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionController.class);
    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @PostMapping
    public ResponseEntity<Notificacion> crearNotificacion(@Valid @RequestBody SoporteDTO dto) {
        Notificacion notificacion = new Notificacion();
        notificacion.setTipo("EMAIL");
        notificacion.setDestinatario("usuario-" + dto.getUsuarioId());
        notificacion.setAsunto(dto.getAsunto());
        notificacion.setMensaje(dto.getDescripcion());
        notificacion.setEstado(dto.getEstado());
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setIdCita(dto.getUsuarioId());

        Notificacion nuevaNotificacion = notificacionService.guardar(notificacion);
        logger.info("Notificacion recibida para el usuario ID: {}", dto.getUsuarioId());

        return new ResponseEntity<>(nuevaNotificacion, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> listarTodas() {
        return ResponseEntity.ok(notificacionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> obtenerPorId(@PathVariable Long id) {
        return notificacionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> actualizar(@PathVariable Long id, @Valid @RequestBody Notificacion notificacion) {
        return notificacionService.buscarPorId(id)
                .map(n -> {
                    notificacion.setId(id);
                    return ResponseEntity.ok(notificacionService.guardar(notificacion));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.eliminarNotificacion(id);
        return ResponseEntity.noContent().build();
    }
}