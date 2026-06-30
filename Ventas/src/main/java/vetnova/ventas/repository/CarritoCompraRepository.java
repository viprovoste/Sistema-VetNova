package vetnova.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vetnova.ventas.model.CarritoCompra;
import vetnova.ventas.model.EstadoCarrito;

import java.util.Optional;

public interface CarritoCompraRepository extends JpaRepository<CarritoCompra, Long> {

    Optional<CarritoCompra> findByClienteIdAndEstado(Long clienteId, EstadoCarrito estado);
}
