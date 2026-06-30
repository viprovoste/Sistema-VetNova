package vetnova.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Referencia externa al microservicio de Autenticacion. */
    private Long clienteId;

    /** Referencia externa al microservicio de Agendamiento y Sucursales (null si es venta web). */
    private Long sucursalId;

    /** Recepcionista o cajero que proceso la venta (referencia externa a Autenticacion). */
    private Long usuarioId;

    /** Carrito de origen, si la venta proviene de una compra web. */
    private Long carritoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoVenta tipoVenta;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private EstadoVenta estado = EstadoVenta.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MetodoPago metodoPago;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Builder.Default
    @Column(precision = 12, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(length = 500)
    private String observaciones;

    private LocalDateTime fechaVenta;

    @PrePersist
    public void prePersist() {
        this.fechaVenta = LocalDateTime.now();
        if (this.estado == null) this.estado = EstadoVenta.PENDIENTE;
        if (this.descuento == null) this.descuento = BigDecimal.ZERO;
    }
}
