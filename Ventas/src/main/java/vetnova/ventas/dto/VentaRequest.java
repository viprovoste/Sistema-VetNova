package vetnova.ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vetnova.ventas.model.MetodoPago;

import java.math.BigDecimal;
import java.util.List;

/** Body para registrar una venta directa en caja (sucursal). Las ventas web se crean a partir del carrito. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequest {

    private Long clienteId;

    @NotNull(message = "La sucursal es obligatoria para una venta en caja")
    private Long sucursalId;

    @NotNull(message = "El usuario (cajero/recepcionista) es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;

    private BigDecimal descuento;

    private String observaciones;

    @NotEmpty(message = "La venta debe incluir al menos un producto")
    private List<@Valid ItemVentaRequest> items;
}
