package vetnova.ventas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vetnova.ventas.client.AuthClient;
import vetnova.ventas.client.dto.UsuarioDTO;
import vetnova.ventas.dto.DocumentoFiscalResponse;
import vetnova.ventas.dto.EmitirFacturaRequest;
import vetnova.ventas.exception.ResourceNotFoundException;
import vetnova.ventas.model.DocumentoFiscal;
import vetnova.ventas.model.TipoDocumento;
import vetnova.ventas.model.TipoVenta;
import vetnova.ventas.model.Venta;
import vetnova.ventas.model.EstadoVenta;
import vetnova.ventas.repository.DocumentoFiscalRepository;
import vetnova.ventas.repository.VentaRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentoFiscalService - pruebas unitarias")
class DocumentoFiscalServiceTest {

    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private DocumentoFiscalRepository documentoFiscalRepository;
    @Mock
    private AuthClient authClient;

    private DocumentoFiscalServiceImpl documentoFiscalService;

    private Venta ventaPagada;

    @BeforeEach
    void setUp() {
        documentoFiscalService = new DocumentoFiscalServiceImpl(ventaRepository, documentoFiscalRepository, authClient);

        ventaPagada = Venta.builder()
                .id(100L).clienteId(50L).tipoVenta(TipoVenta.SUCURSAL).estado(EstadoVenta.PAGADA)
                .subtotal(new BigDecimal("11900")).descuento(BigDecimal.ZERO).total(new BigDecimal("11900"))
                .build();
    }

    @Nested
    @DisplayName("Emisión de boleta")
    class EmitirBoleta {

        @Test
        @DisplayName("Calcula correctamente neto e IVA y obtiene el RUT del cliente vía Autenticación")
        void emiteBoletaCorrectamente() {
            UsuarioDTO cliente = new UsuarioDTO(50L, "Carla", "Soto", "carla@vetnova.cl", "11111111-1");

            when(ventaRepository.findById(100L)).thenReturn(Optional.of(ventaPagada));
            when(documentoFiscalRepository.existsByVentaId(100L)).thenReturn(false);
            when(authClient.obtenerUsuarioPorId(50L)).thenReturn(cliente);
            when(documentoFiscalRepository.save(any(DocumentoFiscal.class))).thenAnswer(inv -> inv.getArgument(0));

            DocumentoFiscalResponse response = documentoFiscalService.emitirBoleta(100L);

            assertThat(response.getTipoDocumento()).isEqualTo(TipoDocumento.BOLETA);
            assertThat(response.getRutCliente()).isEqualTo("11111111-1");
            assertThat(response.getTotalConIva()).isEqualByComparingTo("11900");
            assertThat(response.getTotalNeto()).isEqualByComparingTo("10000");
            assertThat(response.getIva()).isEqualByComparingTo("1900");
            assertThat(response.getNumeroDocumento()).isEqualTo("BOL-00000100");
        }

        @Test
        @DisplayName("Rechaza emitir un segundo documento para la misma venta")
        void rechazaDocumentoDuplicado() {
            when(ventaRepository.findById(100L)).thenReturn(Optional.of(ventaPagada));
            when(documentoFiscalRepository.existsByVentaId(100L)).thenReturn(true);

            assertThatThrownBy(() -> documentoFiscalService.emitirBoleta(100L))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(documentoFiscalRepository, never()).save(any());
        }

        @Test
        @DisplayName("Lanza excepción si la venta no existe")
        void lanzaExcepcionSiVentaNoExiste() {
            when(ventaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> documentoFiscalService.emitirBoleta(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Emisión de factura")
    class EmitirFactura {

        @Test
        @DisplayName("Usa la razón social y RUT de empresa entregados, sin consultar a Autenticación")
        void emiteFacturaConDatosDeEmpresa() {
            EmitirFacturaRequest request = new EmitirFacturaRequest("VetMed Distribuidora SpA", "77888999-2");

            when(ventaRepository.findById(100L)).thenReturn(Optional.of(ventaPagada));
            when(documentoFiscalRepository.existsByVentaId(100L)).thenReturn(false);
            when(documentoFiscalRepository.save(any(DocumentoFiscal.class))).thenAnswer(inv -> inv.getArgument(0));

            DocumentoFiscalResponse response = documentoFiscalService.emitirFactura(100L, request);

            assertThat(response.getTipoDocumento()).isEqualTo(TipoDocumento.FACTURA);
            assertThat(response.getRazonSocial()).isEqualTo("VetMed Distribuidora SpA");
            assertThat(response.getRutCliente()).isEqualTo("77888999-2");
            assertThat(response.getNumeroDocumento()).isEqualTo("FAC-00000100");
            verify(authClient, never()).obtenerUsuarioPorId(any());
        }
    }

    @Nested
    @DisplayName("Consulta de documento por venta")
    class ObtenerPorVenta {

        @Test
        @DisplayName("Devuelve el documento fiscal existente de una venta")
        void devuelveDocumentoExistente() {
            DocumentoFiscal documento = DocumentoFiscal.builder()
                    .id(1L).ventaId(100L).tipoDocumento(TipoDocumento.BOLETA).numeroDocumento("BOL-00000100")
                    .totalNeto(new BigDecimal("10000")).iva(new BigDecimal("1900")).totalConIva(new BigDecimal("11900"))
                    .build();
            when(documentoFiscalRepository.findByVentaId(100L)).thenReturn(Optional.of(documento));

            DocumentoFiscalResponse response = documentoFiscalService.obtenerPorVenta(100L);

            assertThat(response.getNumeroDocumento()).isEqualTo("BOL-00000100");
        }

        @Test
        @DisplayName("Lanza excepción si la venta no tiene documento fiscal emitido")
        void lanzaExcepcionSiNoTieneDocumento() {
            when(documentoFiscalRepository.findByVentaId(200L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> documentoFiscalService.obtenerPorVenta(200L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
