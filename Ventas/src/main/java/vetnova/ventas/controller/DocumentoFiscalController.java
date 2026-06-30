package vetnova.ventas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vetnova.ventas.dto.DocumentoFiscalResponse;
import vetnova.ventas.dto.EmitirFacturaRequest;
import vetnova.ventas.service.DocumentoFiscalService;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
@Tag(name = "Documentos Fiscales", description = "Emisión de boletas y facturas")
public class DocumentoFiscalController {

    private final DocumentoFiscalService documentoFiscalService;

    @GetMapping("/venta/{ventaId}")
    @Operation(summary = "Obtener el documento fiscal de una venta")
    public ResponseEntity<DocumentoFiscalResponse> obtenerPorVenta(@PathVariable Long ventaId) {
        return ResponseEntity.ok(documentoFiscalService.obtenerPorVenta(ventaId));
    }

    @PostMapping("/venta/{ventaId}/boleta")
    @Operation(summary = "Emitir boleta", description = "Obtiene el RUT del cliente consultando al microservicio de Autenticación")
    public ResponseEntity<DocumentoFiscalResponse> emitirBoleta(@PathVariable Long ventaId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentoFiscalService.emitirBoleta(ventaId));
    }

    @PostMapping("/venta/{ventaId}/factura")
    @Operation(summary = "Emitir factura", description = "Requiere razón social; opcionalmente un RUT de empresa distinto al del cliente")
    public ResponseEntity<DocumentoFiscalResponse> emitirFactura(@PathVariable Long ventaId,
                                                                    @Valid @RequestBody EmitirFacturaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentoFiscalService.emitirFactura(ventaId, request));
    }
}
