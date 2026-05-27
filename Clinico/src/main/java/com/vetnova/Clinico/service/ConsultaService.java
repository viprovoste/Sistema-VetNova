package com.vetnova.Clinico.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetnova.Clinico.model.Consulta;
import com.vetnova.Clinico.model.FichaClinica;
import com.vetnova.Clinico.repository.ConsultaRepository;
import com.vetnova.Clinico.repository.FichaClinicaRepository;

@Service
public class ConsultaService {
    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private FichaClinicaRepository fichaClinicaRepository;

    public Consulta guardarConsulta(Long idFicha, Consulta consulta) {
        Optional<FichaClinica> fichaExistente = fichaClinicaRepository.findById(idFicha);
 
        if (fichaExistente.isPresent()) {
            consulta.setFichaClinica(fichaExistente.get());
            return consultaRepository.save(consulta);
        }
        return null;
    }
 
    public List<Consulta> listarConsultasPorFicha(Long idFicha) {
        return consultaRepository.findByFichaClinicaIdFicha(idFicha);
    }
    
    public Consulta actualizarConsulta(Long idConsulta, Consulta datosaCambiar) {
        Optional<Consulta> consultaExistente = consultaRepository.findById(idConsulta);
 
        if (consultaExistente.isPresent()) {
            Consulta consulta = consultaExistente.get();
 

            if (datosaCambiar.getMotivoConsulta() != null) {
                consulta.setMotivoConsulta(datosaCambiar.getMotivoConsulta());
            }
            if (datosaCambiar.getTemperatura() != null) {
                consulta.setTemperatura(datosaCambiar.getTemperatura());
            }
            if (datosaCambiar.getPesoActual() != null) {
                consulta.setPesoActual(datosaCambiar.getPesoActual());
            }
            if (datosaCambiar.getObservaciones() != null) {
                consulta.setObservaciones(datosaCambiar.getObservaciones());
            }
            if (datosaCambiar.getDiagnostico() != null) {
                consulta.setDiagnostico(datosaCambiar.getDiagnostico());
            }
            if (datosaCambiar.getRecetaMedica() != null) {
                consulta.setRecetaMedica(datosaCambiar.getRecetaMedica());
            }
            if (datosaCambiar.getIndicaciones() != null) {
                consulta.setIndicaciones(datosaCambiar.getIndicaciones());
            }
            if (datosaCambiar.getOrdenExamen() != null) {
                consulta.setOrdenExamen(datosaCambiar.getOrdenExamen());
            }
            return consultaRepository.save(consulta);
        }
        return null;
    }
    
}