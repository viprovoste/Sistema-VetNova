package vetnova.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vetnova.ventas.model.DetalleVenta;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByVentaId(Long ventaId);
}
