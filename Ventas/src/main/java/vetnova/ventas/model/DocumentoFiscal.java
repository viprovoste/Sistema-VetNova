package vetnova.ventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "documentos_fiscales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "venta_id", nullable = false, unique = true)
    private Long ventaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoDocumento tipoDocumento;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroDocumento;

    @Column(length = 12)
    private String rutCliente;

    /** Solo se completa para FACTURA. */
    @Column(length = 150)
    private String razonSocial;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalNeto;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal iva;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalConIva;

    private LocalDateTime fechaEmision;

    @PrePersist
    public void prePersist() {
        this.fechaEmision = LocalDateTime.now();
    }
}
