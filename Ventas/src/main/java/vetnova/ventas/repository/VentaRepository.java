package vetnova.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vetnova.ventas.model.Venta;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByClienteId(Long clienteId);

    List<Venta> findBySucursalId(Long sucursalId);
}
