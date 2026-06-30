package vetnova.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vetnova.ventas.model.EstadoCarrito;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoResponse {
    private Long id;
    private Long clienteId;
    private EstadoCarrito estado;
    private List<ItemCarritoResponse> items;
    private BigDecimal total;
}
