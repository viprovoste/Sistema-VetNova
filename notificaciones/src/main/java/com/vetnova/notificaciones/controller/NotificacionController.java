package com.vetnova.notificaciones.controller;

import com.vetnova.notificaciones.dto.SoporteDTO;
import com.vetnova.notificaciones.model.Notificacion;
import com.vetnova.notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

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

        System.out.println("¡Éxito! Notificación recibida para el usuario ID: " + dto.getUsuarioId());

        return new ResponseEntity<>(nuevaNotificacion, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> listarTodas() {
        return new ResponseEntity<>(notificacionService.listarTodas(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> obtenerPorId(@PathVariable Long id) {
        return notificacionService.listarTodas().stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> actualizar(@PathVariable Long id, @Valid @RequestBody Notificacion notificacion) {
        notificacion.setId(id);
        Notificacion actualizada = notificacionService.guardar(notificacion);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.eliminarNotificacion(id);
        return ResponseEntity.noContent().build();
    }
}