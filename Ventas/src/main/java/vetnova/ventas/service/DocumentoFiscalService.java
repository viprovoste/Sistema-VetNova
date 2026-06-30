package vetnova.ventas.service;

import vetnova.ventas.dto.DocumentoFiscalResponse;
import vetnova.ventas.dto.EmitirFacturaRequest;

public interface DocumentoFiscalService {

    DocumentoFiscalResponse emitirBoleta(Long ventaId);

    DocumentoFiscalResponse emitirFactura(Long ventaId, EmitirFacturaRequest request);

    DocumentoFiscalResponse obtenerPorVenta(Long ventaId);
}
