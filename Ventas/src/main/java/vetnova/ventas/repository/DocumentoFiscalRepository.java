package vetnova.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vetnova.ventas.model.DocumentoFiscal;

import java.util.Optional;

public interface DocumentoFiscalRepository extends JpaRepository<DocumentoFiscal, Long> {

    Optional<DocumentoFiscal> findByVentaId(Long ventaId);

    boolean existsByVentaId(Long ventaId);
}
