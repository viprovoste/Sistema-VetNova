package vetnova.inventario.service;

import vetnova.inventario.dto.StockResponse;
import vetnova.inventario.dto.VerificarStockRequest;
import vetnova.inventario.dto.VerificarStockResponse;

import java.util.List;

public interface StockService {

    List<StockResponse> listarTodo();

    List<StockResponse> listarPorSucursal(Long sucursalId);

    List<StockResponse> listarPorProducto(Long productoId);

    List<StockResponse> listarBajoMinimo();

    VerificarStockResponse verificarDisponibilidad(VerificarStockRequest request);
}
