package vetnova.ventas.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vetnova.ventas.dto.DocumentoFiscalResponse;
import vetnova.ventas.dto.EmitirFacturaRequest;
import vetnova.ventas.model.TipoDocumento;
import vetnova.ventas.service.DocumentoFiscalService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentoFiscalController - pruebas unitarias")
class DocumentoFiscalControllerTest {

    @Mock
    private DocumentoFiscalService documentoFiscalService;

    private DocumentoFiscalController controller;

    private DocumentoFiscalResponse documentoResponse;

    @BeforeEach
    void setUp() {
        controller = new DocumentoFiscalController(documentoFiscalService);
        documentoResponse = DocumentoFiscalResponse.builder().id(1L).ventaId(100L)
                .tipoDocumento(TipoDocumento.BOLETA).numeroDocumento("BOL-00000100").build();
    }

    @Test
    @DisplayName("obtenerPorVenta() delega en el servicio")
    void obtenerPorVenta() {
        when(documentoFiscalService.obtenerPorVenta(100L)).thenReturn(documentoResponse);

        controller.obtenerPorVenta(100L);

        verify(documentoFiscalService).obtenerPorVenta(100L);
    }

    @Test
    @DisplayName("emitirBoleta() responde 201 CREATED")
    void emitirBoleta() {
        when(documentoFiscalService.emitirBoleta(100L)).thenReturn(documentoResponse);

        ResponseEntity<DocumentoFiscalResponse> response = controller.emitirBoleta(100L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("emitirFactura() responde 201 CREATED")
    void emitirFactura() {
        EmitirFacturaRequest request = new EmitirFacturaRequest("VetMed SpA", "77888999-2");
        when(documentoFiscalService.emitirFactura(100L, request)).thenReturn(documentoResponse);

        ResponseEntity<DocumentoFiscalResponse> response = controller.emitirFactura(100L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
