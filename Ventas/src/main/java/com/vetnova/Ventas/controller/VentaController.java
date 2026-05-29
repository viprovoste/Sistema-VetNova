package com.vetnova.Ventas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetnova.Ventas.dto.VentaRequest;
import com.vetnova.Ventas.service.VentaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {
    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<String> crearVenta(@RequestBody VentaRequest request) {
        String respuesta = ventaService.ejecutarVenta(request);
        return ResponseEntity.ok(respuesta);
    }
}
