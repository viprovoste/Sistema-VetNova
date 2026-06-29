package com.soporte.soporte.controller;

import com.soporte.soporte.model.MensajeSoporte;
import com.soporte.soporte.service.IMensajeSoporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/soportes/{soporteId}/mensajes")
public class MensajeSoporteController {

    private final IMensajeSoporteService mensajeService;

    public MensajeSoporteController(IMensajeSoporteService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @Operation(summary = "Agregar mensaje a un soporte")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Mensaje agregado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Soporte no encontrado")
    })
    @PostMapping
    public ResponseEntity<MensajeSoporte> agregar(@PathVariable Long soporteId,
                                                   @Valid @RequestBody MensajeSoporte mensaje) {
        MensajeSoporte nuevo = mensajeService.agregarMensaje(soporteId, mensaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @Operation(summary = "Listar mensajes de un soporte")
    @ApiResponse(responseCode = "200", description = "Lista de mensajes retornada exitosamente")
    @GetMapping
    public ResponseEntity<List<MensajeSoporte>> listar(@PathVariable Long soporteId) {
        return ResponseEntity.ok(mensajeService.listarMensajes(soporteId));
    }

    @Operation(summary = "Eliminar mensaje por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Mensaje eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Mensaje no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long soporteId,
                                          @PathVariable Long id) {
        mensajeService.eliminarMensaje(id);
        return ResponseEntity.noContent().build();
    }
}