package com.soporte.soporte.controller;

import com.soporte.soporte.model.Soporte;
import com.soporte.soporte.service.ISoporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/soportes")
public class SoporteController {

    private final ISoporteService soporteService;

    public SoporteController(ISoporteService soporteService) {
        this.soporteService = soporteService;
    }

    @Operation(summary = "Crear un nuevo soporte")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Soporte creado exitosamente"),
        @ApiResponse(responseCode = "409", description = "Soporte duplicado - el usuario ya tiene un soporte abierto con el mismo asunto")
    })
    @PostMapping
    public ResponseEntity<?> crearSoporte(@Valid @RequestBody Soporte soporte) {

        if (soporteService.existeDuplicado(soporte.getUsuarioId(), soporte.getAsunto())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", HttpStatus.CONFLICT.value(),
                    "error", "Soporte duplicado",
                    "detalles", "El usuario ya tiene un soporte abierto con el mismo asunto."
                )
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(soporteService.guardar(soporte));
    }

    @Operation(summary = "Listar todos los soportes")
    @ApiResponse(responseCode = "200", description = "Lista de soportes retornada exitosamente")
    @GetMapping
    public ResponseEntity<List<Soporte>> listarTodos() {
        return ResponseEntity.ok(soporteService.listarTodos());
    }

    @Operation(summary = "Obtener soporte por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Soporte encontrado"),
        @ApiResponse(responseCode = "404", description = "Soporte no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Soporte> obtenerPorId(@PathVariable Long id) {
        return soporteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar soporte existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Soporte actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Soporte no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Soporte> actualizar(@PathVariable Long id, @Valid @RequestBody Soporte soporte) {
        return soporteService.buscarPorId(id)
                .map(s -> {
                    soporte.setId(id);
                    return ResponseEntity.ok(soporteService.guardar(soporte));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar soporte por ID")
    @ApiResponse(responseCode = "204", description = "Soporte eliminado exitosamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        soporteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}