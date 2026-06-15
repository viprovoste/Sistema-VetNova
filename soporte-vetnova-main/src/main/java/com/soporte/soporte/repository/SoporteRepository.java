package com.soporte.soporte.repository;

import com.soporte.soporte.model.Soporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoporteRepository extends JpaRepository<Soporte, Long> {
    boolean existsByUsuarioIdAndAsuntoAndEstadoNot(Long usuarioId, String asunto, String estado);
}