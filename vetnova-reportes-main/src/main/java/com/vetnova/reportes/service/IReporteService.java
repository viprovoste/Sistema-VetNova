package com.vetnova.reportes.service;

import com.vetnova.reportes.model.Reporte;

import java.util.List;
import java.util.Optional;

public interface IReporteService {
    Reporte guardarReporte(Reporte reporte);
    List<Reporte> listarTodos();
    Optional<Reporte> buscarPorId(Long id);
    void eliminarReporte(Long id);
}