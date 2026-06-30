package vetnova.ventas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vetnova.ventas.dto.PagarVentaRequest;
import vetnova.ventas.dto.VentaRequest;
import vetnova.ventas.dto.VentaResponse;
import vetnova.ventas.service.VentaService;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "Ventas en sucursal (caja) y ventas web (carrito de compra)")
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    @Operation(summary = "Listar todas las ventas")
    public ResponseEntity<List<VentaResponse>> listarTodas() {
        return ResponseEntity.ok(ventaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener el detalle de una venta")
    public ResponseEntity<VentaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.obtenerPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Historial de compras de un cliente")
    public ResponseEntity<List<VentaResponse>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ventaService.listarPorCliente(clienteId));
    }

    @GetMapping("/sucursal/{sucursalId}")
    @Operation(summary = "Ventas realizadas en una sucursal")
    public ResponseEntity<List<VentaResponse>> listarPorSucursal(@PathVariable Long sucursalId) {
        return ResponseEntity.ok(ventaService.listarPorSucursal(sucursalId));
    }

    @PostMapping
    @Operation(summary = "Registrar venta directa en caja", description = "Usado por Recepcionista/Cajero en sucursal")
    public ResponseEntity<VentaResponse> crearVentaDirecta(@Valid @RequestBody VentaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.crearVentaDirecta(request));
    }

    @PostMapping("/carrito/{carritoId}/confirmar")
    @Operation(summary = "Confirmar un carrito web como venta", description = "Convierte un carrito ACTIVO en una venta PENDIENTE de pago")
    public ResponseEntity<VentaResponse> confirmarCarrito(@PathVariable Long carritoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.confirmarCarrito(carritoId));
    }

    @PostMapping("/{id}/pagar")
    @Operation(summary = "Confirmar el pago de una venta",
            description = "Registra automáticamente la salida de stock en el microservicio de Inventario por cada producto vendido")
    public ResponseEntity<VentaResponse> pagar(@PathVariable Long id, @Valid @RequestBody PagarVentaRequest request) {
        return ResponseEntity.ok(ventaService.pagar(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una venta no pagada")
    public ResponseEntity<VentaResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.cancelar(id));
    }
}
