package vetnova.ventas.service;

import vetnova.ventas.dto.ActualizarCantidadRequest;
import vetnova.ventas.dto.CarritoResponse;
import vetnova.ventas.dto.ItemCarritoRequest;

public interface CarritoService {

    CarritoResponse obtenerCarritoActivo(Long clienteId);

    CarritoResponse agregarItem(Long clienteId, ItemCarritoRequest request);

    CarritoResponse actualizarCantidad(Long carritoId, Long itemId, ActualizarCantidadRequest request);

    CarritoResponse eliminarItem(Long carritoId, Long itemId);

    CarritoResponse vaciarCarrito(Long carritoId);
}
