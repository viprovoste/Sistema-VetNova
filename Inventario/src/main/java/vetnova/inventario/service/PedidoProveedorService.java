package vetnova.inventario.service;

import vetnova.inventario.dto.ActualizarEstadoPedidoRequest;
import vetnova.inventario.dto.PedidoProveedorRequest;
import vetnova.inventario.dto.PedidoProveedorResponse;
import vetnova.inventario.dto.RecibirPedidoRequest;

import java.util.List;

public interface PedidoProveedorService {

    PedidoProveedorResponse crear(PedidoProveedorRequest request);

    List<PedidoProveedorResponse> listarTodos();

    PedidoProveedorResponse obtenerPorId(Long id);

    PedidoProveedorResponse actualizarEstado(Long id, ActualizarEstadoPedidoRequest request);

    PedidoProveedorResponse recibir(Long id, RecibirPedidoRequest request);
}
