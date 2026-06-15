package com.vetnova.reportes.controller;

import com.vetnova.reportes.dto.SoporteDTO;
import com.vetnova.reportes.model.Reporte;
import com.vetnova.reportes.service.IReporteService;
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

    @PostMapping
    public ResponseEntity<Reporte> crearReporte(@Valid @RequestBody SoporteDTO dto) {
        Reporte reporte = new Reporte();
        reporte.setTotalAtenciones(dto.getUsuarioId() != null ? 1 : 0);
        reporte.setTotalAlertasGeneradas(dto.getEstado() != null ? 1 : 0);
        reporte.setRendimientoGlobal(dto.getEstado() != null &&
                dto.getEstado().equalsIgnoreCase("RESUELTO") ? 100.0 : 50.0);

        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardarReporte(reporte));
    }

    @GetMapping
    public ResponseEntity<List<Reporte>> obtenerTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reporte> obtenerPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reporte> actualizar(@PathVariable Long id, @Valid @RequestBody Reporte reporte) {
        return service.buscarPorId(id)
                .map(r -> {
                    reporte.setId(id);
                    return ResponseEntity.ok(service.guardarReporte(reporte));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarReporte(id);
        return ResponseEntity.noContent().build();
    }
}