package vetnova.ventas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vetnova.ventas.dto.ActualizarCantidadRequest;
import vetnova.ventas.dto.CarritoResponse;
import vetnova.ventas.dto.ItemCarritoRequest;
import vetnova.ventas.service.CarritoService;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
@Tag(name = "Carrito de Compra", description = "Carrito de compra web del cliente (canal e-commerce)")
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener (o crear) el carrito activo de un cliente")
    public ResponseEntity<CarritoResponse> obtenerCarritoActivo(@PathVariable Long clienteId) {
        return ResponseEntity.ok(carritoService.obtenerCarritoActivo(clienteId));
    }

    @PostMapping("/cliente/{clienteId}/items")
    @Operation(summary = "Agregar producto al carrito",
            description = "Consulta el precio vigente y verifica stock disponible en el microservicio de Inventario")
    public ResponseEntity<CarritoResponse> agregarItem(@PathVariable Long clienteId,
                                                          @Valid @RequestBody ItemCarritoRequest request) {
        return ResponseEntity.ok(carritoService.agregarItem(clienteId, request));
    }

    @PutMapping("/{carritoId}/items/{itemId}")
    @Operation(summary = "Cambiar la cantidad de un producto en el carrito")
    public ResponseEntity<CarritoResponse> actualizarCantidad(@PathVariable Long carritoId,
                                                                 @PathVariable Long itemId,
                                                                 @Valid @RequestBody ActualizarCantidadRequest request) {
        return ResponseEntity.ok(carritoService.actualizarCantidad(carritoId, itemId, request));
    }

    @DeleteMapping("/{carritoId}/items/{itemId}")
    @Operation(summary = "Quitar un producto del carrito")
    public ResponseEntity<CarritoResponse> eliminarItem(@PathVariable Long carritoId, @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.eliminarItem(carritoId, itemId));
    }

    @DeleteMapping("/{carritoId}/vaciar")
    @Operation(summary = "Vaciar el carrito completo")
    public ResponseEntity<CarritoResponse> vaciarCarrito(@PathVariable Long carritoId) {
        return ResponseEntity.ok(carritoService.vaciarCarrito(carritoId));
    }
}
