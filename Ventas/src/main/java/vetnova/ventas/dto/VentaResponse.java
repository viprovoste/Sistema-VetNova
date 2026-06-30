package vetnova.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vetnova.ventas.model.EstadoVenta;
import vetnova.ventas.model.MetodoPago;
import vetnova.ventas.model.TipoVenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaResponse {
    private Long id;
    private Long clienteId;
    private Long sucursalId;
    private Long usuarioId;
    private TipoVenta tipoVenta;
    private EstadoVenta estado;
    private MetodoPago metodoPago;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal total;
    private String observaciones;
    private LocalDateTime fechaVenta;
    private List<DetalleVentaResponse> detalles;
}
