package vetnova.ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vetnova.ventas.model.TipoDocumento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoFiscalResponse {
    private Long id;
    private Long ventaId;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String rutCliente;
    private String razonSocial;
    private BigDecimal totalNeto;
    private BigDecimal iva;
    private BigDecimal totalConIva;
    private LocalDateTime fechaEmision;
}
