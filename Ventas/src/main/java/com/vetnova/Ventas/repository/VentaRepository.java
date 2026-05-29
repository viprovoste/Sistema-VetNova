package com.vetnova.Ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vetnova.Ventas.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    
}
