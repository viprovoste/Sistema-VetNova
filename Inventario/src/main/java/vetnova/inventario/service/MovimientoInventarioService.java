package vetnova.inventario.service;

import vetnova.inventario.dto.MovimientoRequest;
import vetnova.inventario.dto.MovimientoResponse;

import java.util.List;

public interface MovimientoInventarioService {

    MovimientoResponse registrarMovimiento(MovimientoRequest request, String tokenUsuario);

    List<MovimientoResponse> listarTodos();

    List<MovimientoResponse> listarPorProducto(Long productoId);

    List<MovimientoResponse> listarPorSucursal(Long sucursalId);
}
