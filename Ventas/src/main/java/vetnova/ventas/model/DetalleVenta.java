package vetnova.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "venta_id", nullable = false)
    private Long ventaId;

    /** Referencia externa al microservicio de Inventario. */
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    /** Snapshot del nombre al momento de la venta. */
    @Column(nullable = false, length = 150)
    private String nombreProducto;

    @Column(nullable = false)
    private Integer cantidad;

    /** Snapshot del precio al momento de la venta. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalLinea;
}
