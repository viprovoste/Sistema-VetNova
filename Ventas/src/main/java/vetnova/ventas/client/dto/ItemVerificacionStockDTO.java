package vetnova.ventas.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVerificacionStockDTO {
    private Long productoId;
    private Integer cantidad;
}
