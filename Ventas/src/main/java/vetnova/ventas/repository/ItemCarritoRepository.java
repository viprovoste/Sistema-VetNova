package vetnova.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vetnova.ventas.model.ItemCarrito;

import java.util.List;
import java.util.Optional;

public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    List<ItemCarrito> findByCarritoId(Long carritoId);

    Optional<ItemCarrito> findByCarritoIdAndProductoId(Long carritoId, Long productoId);

    void deleteByCarritoId(Long carritoId);
}
