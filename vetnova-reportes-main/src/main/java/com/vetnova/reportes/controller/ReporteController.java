package com.vetnova.reportes.controller;

import com.vetnova.reportes.dto.SoporteDTO;
import com.vetnova.reportes.model.Reporte;
import com.vetnova.reportes.service.IReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final IReporteService service;

    public ReporteController(IReporteService service) {
        this.service = service;
    }

    @Operation(summary = "Crear un nuevo reporte")
    @ApiResponse(responseCode = "201", description = "Reporte creado exitosamente")
    @PostMapping
    public ResponseEntity<Reporte> crearReporte(@Valid @RequestBody SoporteDTO dto) {
        Reporte reporte = new Reporte();
        reporte.setTotalAtenciones(dto.getUsuarioId() != null ? 1 : 0);
        reporte.setTotalAlertasGeneradas(dto.getEstado() != null ? 1 : 0);
        reporte.setRendimientoGlobal(dto.getEstado() != null &&
                dto.getEstado().equalsIgnoreCase("RESUELTO") ? 100.0 : 50.0);

        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardarReporte(reporte));
    }

    @Operation(summary = "Listar todos los reportes")
    @ApiResponse(responseCode = "200", description = "Lista de reportes retornada exitosamente")
    @GetMapping
    public ResponseEntity<List<Reporte>> obtenerTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Obtener reporte por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte encontrado"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Reporte> obtenerPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar reporte existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Reporte> actualizar(@PathVariable Long id, @Valid @RequestBody Reporte reporte) {
        return service.buscarPorId(id)
                .map(r -> {
                    reporte.setId(id);
                    return ResponseEntity.ok(service.guardarReporte(reporte));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar reporte por ID")
    @ApiResponse(responseCode = "204", description = "Reporte eliminado exitosamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarReporte(id);
        return ResponseEntity.noContent().build();
    }
}