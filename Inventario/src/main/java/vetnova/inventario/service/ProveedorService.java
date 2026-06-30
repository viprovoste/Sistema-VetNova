package vetnova.inventario.service;

import vetnova.inventario.dto.ProveedorRequest;
import vetnova.inventario.dto.ProveedorResponse;

import java.util.List;

public interface ProveedorService {

    ProveedorResponse crear(ProveedorRequest request);

    List<ProveedorResponse> listarTodos();

    ProveedorResponse obtenerPorId(Long id);

    ProveedorResponse actualizar(Long id, ProveedorRequest request);

    ProveedorResponse desactivar(Long id);
}
