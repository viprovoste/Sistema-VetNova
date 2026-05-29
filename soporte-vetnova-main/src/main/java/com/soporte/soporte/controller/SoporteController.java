package com.soporte.soporte.controller;

import com.soporte.soporte.model.Soporte;
import com.soporte.soporte.service.SoporteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/soportes")
public class SoporteController {

    private final SoporteService soporteService;

    public SoporteController(SoporteService soporteService) {
        this.soporteService = soporteService;
    }

    @PostMapping
    public ResponseEntity<Soporte> crearSoporte(@Valid @RequestBody Soporte soporte) {
        return ResponseEntity.status(HttpStatus.CREATED).body(soporteService.guardar(soporte));
    }

    @GetMapping
    public ResponseEntity<List<Soporte>> listarTodos() {
        return ResponseEntity.ok(soporteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Soporte> obtenerPorId(@PathVariable Long id) {
        return soporteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Soporte> actualizar(@PathVariable Long id, @Valid @RequestBody Soporte soporte) {
        return soporteService.buscarPorId(id)
                .map(s -> {
                    soporte.setId(id);
                    return ResponseEntity.ok(soporteService.guardar(soporte));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        soporteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}