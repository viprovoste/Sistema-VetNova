package vetnova.ventas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vetnova.ventas.client.AuthClient;
import vetnova.ventas.client.dto.UsuarioDTO;
import vetnova.ventas.dto.DocumentoFiscalResponse;
import vetnova.ventas.dto.EmitirFacturaRequest;
import vetnova.ventas.exception.ResourceNotFoundException;
import vetnova.ventas.model.DocumentoFiscal;
import vetnova.ventas.model.TipoDocumento;
import vetnova.ventas.model.Venta;
import vetnova.ventas.repository.DocumentoFiscalRepository;
import vetnova.ventas.repository.VentaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class DocumentoFiscalServiceImpl implements DocumentoFiscalService {

    private static final BigDecimal FACTOR_IVA = new BigDecimal("1.19");

    private final VentaRepository ventaRepository;
    private final DocumentoFiscalRepository documentoFiscalRepository;
    private final AuthClient authClient;

    @Override
    @Transactional
    public DocumentoFiscalResponse emitirBoleta(Long ventaId) {
        return emitirDocumento(ventaId, TipoDocumento.BOLETA, null, null);
    }

    /** Para una FACTURA se exige razón social; el RUT puede ser el de la empresa o el del cliente. */
    @Override
    @Transactional
    public DocumentoFiscalResponse emitirFactura(Long ventaId, EmitirFacturaRequest request) {
        return emitirDocumento(ventaId, TipoDocumento.FACTURA, request.getRazonSocial(), request.getRutEmpresa());
    }

    @Override
    public DocumentoFiscalResponse obtenerPorVenta(Long ventaId) {
        DocumentoFiscal documento = documentoFiscalRepository.findByVentaId(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La venta " + ventaId + " no tiene un documento fiscal emitido"));
        return mapearResponse(documento);
    }

    private DocumentoFiscalResponse emitirDocumento(Long ventaId, TipoDocumento tipo, String razonSocial, String rutManual) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la venta con id: " + ventaId));

        if (documentoFiscalRepository.existsByVentaId(ventaId)) {
            throw new IllegalArgumentException("La venta " + ventaId + " ya tiene un documento fiscal emitido");
        }

        String rutCliente = rutManual;
        if (rutCliente == null && venta.getClienteId() != null) {
            UsuarioDTO cliente = authClient.obtenerUsuarioPorId(venta.getClienteId());
            rutCliente = cliente.getRut();
        }

        BigDecimal totalNeto = venta.getTotal().divide(FACTOR_IVA, 0, RoundingMode.HALF_UP);
        BigDecimal iva = venta.getTotal().subtract(totalNeto);

        DocumentoFiscal documento = DocumentoFiscal.builder()
                .ventaId(venta.getId())
                .tipoDocumento(tipo)
                .numeroDocumento(generarNumeroDocumento(tipo, venta.getId()))
                .rutCliente(rutCliente)
                .razonSocial(razonSocial)
                .totalNeto(totalNeto)
                .iva(iva)
                .totalConIva(venta.getTotal())
                .build();

        return mapearResponse(documentoFiscalRepository.save(documento));
    }

    private String generarNumeroDocumento(TipoDocumento tipo, Long ventaId) {
        String prefijo = (tipo == TipoDocumento.BOLETA) ? "BOL" : "FAC";
        return prefijo + "-" + String.format("%08d", ventaId);
    }

    private DocumentoFiscalResponse mapearResponse(DocumentoFiscal documento) {
        return DocumentoFiscalResponse.builder()
                .id(documento.getId())
                .ventaId(documento.getVentaId())
                .tipoDocumento(documento.getTipoDocumento())
                .numeroDocumento(documento.getNumeroDocumento())
                .rutCliente(documento.getRutCliente())
                .razonSocial(documento.getRazonSocial())
                .totalNeto(documento.getTotalNeto())
                .iva(documento.getIva())
                .totalConIva(documento.getTotalConIva())
                .fechaEmision(documento.getFechaEmision())
                .build();
    }
}
