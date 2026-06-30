package vetnova.inventario.service;

import vetnova.inventario.dto.ProductoRequest;
import vetnova.inventario.dto.ProductoResponse;
import vetnova.inventario.model.CategoriaProducto;
import vetnova.inventario.model.TipoUso;

import java.util.List;

public interface ProductoService {

    ProductoResponse crear(ProductoRequest request);

    List<ProductoResponse> listarTodos();

    ProductoResponse obtenerPorId(Long id);

    List<ProductoResponse> listarPorCategoria(CategoriaProducto categoria);

    List<ProductoResponse> listarPorTipoUso(TipoUso tipoUso);

    ProductoResponse actualizar(Long id, ProductoRequest request);

    ProductoResponse desactivar(Long id);
}
