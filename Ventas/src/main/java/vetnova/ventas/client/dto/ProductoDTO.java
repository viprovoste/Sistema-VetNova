package vetnova.ventas.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Espejo de ProductoResponse del microservicio de Inventario (solo los campos que Ventas necesita). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Boolean activo;
}
