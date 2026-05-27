package com.soporte.soporte.repository;

import com.soporte.soporte.model.MensajeSoporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeSoporteRepository extends JpaRepository<MensajeSoporte, Long> {
    List<MensajeSoporte> findBySoporteId(Long soporteId);
}