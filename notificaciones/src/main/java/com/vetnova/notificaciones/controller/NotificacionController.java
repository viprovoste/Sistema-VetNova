package com.vetnova.notificaciones.controller;

import com.vetnova.notificaciones.dto.NotificacionRequestDTO;
import com.vetnova.notificaciones.model.Notificacion;
import com.vetnova.notificaciones.service.INotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionController.class);
    private final INotificacionService notificacionService;

    public NotificacionController(INotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @Operation(summary = "Crear una nueva notificación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Notificación creada exitosamente"),
        @ApiResponse(responseCode = "409", description = "Notificación duplicada - ya existe una con el mismo idCita, tipo y estado")
    })
    @PostMapping
    public ResponseEntity<?> crearNotificacion(@Valid @RequestBody NotificacionRequestDTO dto) {

        if (notificacionService.existeDuplicado(dto.getIdCita(), dto.getTipo(), dto.getEstado())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", HttpStatus.CONFLICT.value(),
                    "error", "Notificación duplicada",
                    "detalles", "Ya existe una notificación con el mismo idCita, tipo y estado."
                )
            );
        }

        Notificacion notificacion = new Notificacion();
        notificacion.setTipo(dto.getTipo());
        notificacion.setDestinatario("usuario-" + dto.getUsuarioId());
        notificacion.setAsunto(dto.getAsunto());
        notificacion.setMensaje(dto.getDescripcion());
        notificacion.setEstado(dto.getEstado());
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setIdCita(dto.getIdCita());

        Notificacion nuevaNotificacion = notificacionService.guardar(notificacion);
        logger.info("Notificacion recibida para el usuario ID: {}", dto.getUsuarioId());

        return new ResponseEntity<>(nuevaNotificacion, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todas las notificaciones")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones retornada exitosamente")
    @GetMapping
    public ResponseEntity<List<Notificacion>> listarTodas() {
        return ResponseEntity.ok(notificacionService.listarTodas());
    }

    @Operation(summary = "Obtener notificación por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación encontrada"),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> obtenerPorId(@PathVariable Long id) {
        return notificacionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar notificación existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> actualizar(@PathVariable Long id, @Valid @RequestBody Notificacion notificacion) {
        return notificacionService.buscarPorId(id)
                .map(n -> {
                    notificacion.setId(id);
                    return ResponseEntity.ok(notificacionService.guardar(notificacion));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar notificación por ID")
    @ApiResponse(responseCode = "204", description = "Notificación eliminada exitosamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.eliminarNotificacion(id);
        return ResponseEntity.noContent().build();
    }
}