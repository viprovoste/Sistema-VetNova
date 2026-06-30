package vetnova.ventas.service;

import vetnova.ventas.dto.PagarVentaRequest;
import vetnova.ventas.dto.VentaRequest;
import vetnova.ventas.dto.VentaResponse;

import java.util.List;

public interface VentaService {

    List<VentaResponse> listarTodas();

    VentaResponse obtenerPorId(Long id);

    List<VentaResponse> listarPorCliente(Long clienteId);

    List<VentaResponse> listarPorSucursal(Long sucursalId);

    VentaResponse crearVentaDirecta(VentaRequest request);

    VentaResponse confirmarCarrito(Long carritoId);

    VentaResponse pagar(Long ventaId, PagarVentaRequest request);

    VentaResponse cancelar(Long ventaId);
}
