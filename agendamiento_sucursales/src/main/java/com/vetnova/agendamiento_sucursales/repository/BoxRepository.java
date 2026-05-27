package com.vetnova.agendamiento_sucursales.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetnova.agendamiento_sucursales.model.Box;
import com.vetnova.agendamiento_sucursales.model.EstadoBox;

@Repository
public interface BoxRepository extends JpaRepository<Box, Long> {
    List<Box> findBySucursalIdSucursal(Long idSucursal);
    List<Box> findBySucursalIdSucursalAndEstado(Long idSucursal, EstadoBox estado);
    
}
