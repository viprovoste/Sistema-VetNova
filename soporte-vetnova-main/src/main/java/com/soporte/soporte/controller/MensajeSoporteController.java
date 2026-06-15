package com.soporte.soporte.controller;

import com.soporte.soporte.model.MensajeSoporte;
import com.soporte.soporte.service.IMensajeSoporteService;
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

    @PostMapping
    public ResponseEntity<MensajeSoporte> agregar(@PathVariable Long soporteId,
                                                   @Valid @RequestBody MensajeSoporte mensaje) {
        MensajeSoporte nuevo = mensajeService.agregarMensaje(soporteId, mensaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping
    public ResponseEntity<List<MensajeSoporte>> listar(@PathVariable Long soporteId) {
        return ResponseEntity.ok(mensajeService.listarMensajes(soporteId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long soporteId,
                                          @PathVariable Long id) {
        mensajeService.eliminarMensaje(id);
        return ResponseEntity.noContent().build();
    }
}