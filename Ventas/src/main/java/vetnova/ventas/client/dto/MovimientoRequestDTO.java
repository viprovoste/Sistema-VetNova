package vetnova.ventas.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Espejo de MovimientoRequest del microservicio de Inventario, usado para registrar la SALIDA al pagar una venta. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoRequestDTO {
    private Long productoId;
    private Long sucursalId;
    private String tipoMovimiento;
    private Integer cantidad;
    private String motivo;
    private Long usuarioId;
    private String referenciaDocumento;
}
