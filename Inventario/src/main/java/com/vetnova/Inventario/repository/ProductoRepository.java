package com.vetnova.Inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vetnova.Inventario.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
}
