package com.vetnova.Ventas.dto;

public record VentaRequest(
    Long productoId,
    Integer cantidad,
    Double precioUnitario
) {
    
}
