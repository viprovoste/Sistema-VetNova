package vetnova.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "items_carrito")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "carrito_id", nullable = false)
    private Long carritoId;

    /** Referencia externa al microservicio de Inventario. */
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    /** Snapshot: nombre del producto al momento de agregarlo (puede cambiar despues en Inventario). */
    @Column(nullable = false, length = 150)
    private String nombreProducto;

    /** Snapshot: precio al momento de agregarlo, para no afectar al cliente con cambios de precio. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private Integer cantidad;

    private LocalDateTime fechaAgregado;

    @PrePersist
    public void prePersist() {
        this.fechaAgregado = LocalDateTime.now();
    }
}
