package com.vetnova.reportes.service;

import com.vetnova.reportes.model.Reporte;
import com.vetnova.reportes.repository.ReporteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReporteService implements IReporteService {

    private static final Logger logger = LoggerFactory.getLogger(ReporteService.class);
    private final ReporteRepository repository;

    public ReporteService(ReporteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Reporte guardarReporte(Reporte reporte) {
        logger.info("LOG: Procesando guardado de nuevo reporte.");
        return repository.save(reporte);
    }

    @Override
    public List<Reporte> listarTodos() {
        logger.info("LOG: Consultando lista completa de reportes.");
        return repository.findAll();
    }

    @Override
    public Optional<Reporte> buscarPorId(Long id) {
        logger.info("LOG: Buscando reporte con ID: {}", id);
        return repository.findById(id);
    }

    @Override
    public void eliminarReporte(Long id) {
        logger.info("LOG: Eliminando reporte con ID: {}", id);
        if (!repository.existsById(id)) {
            throw new RuntimeException("Reporte no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}