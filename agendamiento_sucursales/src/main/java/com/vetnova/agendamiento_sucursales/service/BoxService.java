package com.vetnova.agendamiento_sucursales.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetnova.agendamiento_sucursales.model.Box;
import com.vetnova.agendamiento_sucursales.model.EstadoBox;
import com.vetnova.agendamiento_sucursales.repository.BoxRepository;

@Service
public class BoxService {
    @Autowired
    private BoxRepository boxRepository;

    public List<Box> listarBoxesPorSucursal(Long idSucursal) {
        return boxRepository.findBySucursalIdSucursal(idSucursal);
    }

    public List<Box> listarBoxesPorEstado(Long idSurcursal, EstadoBox estado) {
        return boxRepository.findBySucursalIdSucursalyEstado(idSurcursal, estado);
    }

    public Box actualizarEstadoBox(Long idBox, EstadoBox nuevoEstado) {
        Optional<Box> boxExistente = boxRepository.findById(idBox);

        if (boxExistente.isPresent()) {
            Box box = boxExistente.get();
            box.setEstado(nuevoEstado);
            return boxRepository.save(box);
        }
        return null;
    }
    
}
