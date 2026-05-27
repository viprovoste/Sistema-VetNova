package com.vetnova.agendamiento_sucursales.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetnova.agendamiento_sucursales.model.Sucursal;
import com.vetnova.agendamiento_sucursales.repository.SucursalRepository;

@Service
public class SurcursalService {
    @Autowired
    private SucursalRepository sucursalRepository;

    public List<Sucursal> listarSurcursales() {
        return sucursalRepository.findAll();
    }

    public Optional<Sucursal> findById(Long id) {
        return sucursalRepository.findById(id);
    }
    
}
