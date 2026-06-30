package com.vetnova.Clinico.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vetnova.Clinico.model.Consulta;
import com.vetnova.Clinico.model.FichaClinica;
import com.vetnova.Clinico.repository.ConsultaRepository;
import com.vetnova.Clinico.repository.FichaClinicaRepository;

public class ConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private FichaClinicaRepository fichaClinicaRepository;

    @InjectMocks
    private ConsultaService consultaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testguardarConsulta() {
        FichaClinica ficha = new FichaClinica();
        ficha.setIdFicha(1L);

        Consulta consulta = new Consulta();
        consulta.setMotivoConsulta("Control general");

        Consulta consultaGuardada = new Consulta();
        consultaGuardada.setIdConsulta(1L);
        consultaGuardada.setMotivoConsulta("Control general");

        when(fichaClinicaRepository.findById(1L)).thenReturn(Optional.of(ficha));
        when(consultaRepository.save(any(Consulta.class))).thenReturn(consultaGuardada);

        Consulta resultado = consultaService.guardarConsulta(1L, consulta);

        assertThat(resultado).isEqualTo(consultaGuardada);
        assertThat(resultado.getIdConsulta()).isEqualTo(1L);
        assertThat(resultado.getMotivoConsulta()).isEqualTo("Control general");

        verify(fichaClinicaRepository).findById(1L);
        verify(consultaRepository).save(consulta);
    }

    @Test
    void testguardarConsultaFichaNoExiste() {
        Consulta consulta = new Consulta();
        consulta.setMotivoConsulta("Control general");

        when(fichaClinicaRepository.findById(1L)).thenReturn(Optional.empty());

        Consulta resultado = consultaService.guardarConsulta(1L, consulta);

        assertThat(resultado).isNull();

        verify(fichaClinicaRepository).findById(1L);
        verify(consultaRepository, never()).save(any(Consulta.class));
    }

    @Test
    void testlistarConsultasPorFicha() {
        Consulta consulta = new Consulta();
        consulta.setIdConsulta(1L);
        consulta.setMotivoConsulta("Control general");

        List<Consulta> consultas = new ArrayList<>();
        consultas.add(consulta);

        when(consultaRepository.findByFichaClinicaIdFicha(1L)).thenReturn(consultas);

        List<Consulta> resultado = consultaService.listarConsultasPorFicha(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado).contains(consulta);

        verify(consultaRepository).findByFichaClinicaIdFicha(1L);
    }

    @Test
    void testactualizarConsulta() {
        Consulta consultaExistente = new Consulta();
        consultaExistente.setIdConsulta(1L);
        consultaExistente.setMotivoConsulta("Control general");

        Consulta datosaCambiar = new Consulta();
        datosaCambiar.setMotivoConsulta("Vacunación");
        datosaCambiar.setTemperatura("38.5");
        datosaCambiar.setPesoActual("22");
        datosaCambiar.setObservaciones("Paciente estable");
        datosaCambiar.setDiagnostico("Sano");
        datosaCambiar.setRecetaMedica("Ninguna");
        datosaCambiar.setIndicaciones("Volver en 6 meses");
        datosaCambiar.setOrdenExamen("Sin orden");

        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consultaExistente));
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Consulta resultado = consultaService.actualizarConsulta(1L, datosaCambiar);

        assertThat(resultado.getMotivoConsulta()).isEqualTo("Vacunación");
        assertThat(resultado.getTemperatura()).isEqualTo("38.5");
        assertThat(resultado.getPesoActual()).isEqualTo("22");
        assertThat(resultado.getObservaciones()).isEqualTo("Paciente estable");
        assertThat(resultado.getDiagnostico()).isEqualTo("Sano");
        assertThat(resultado.getRecetaMedica()).isEqualTo("Ninguna");
        assertThat(resultado.getIndicaciones()).isEqualTo("Volver en 6 meses");
        assertThat(resultado.getOrdenExamen()).isEqualTo("Sin orden");

        verify(consultaRepository).findById(1L);
        verify(consultaRepository).save(consultaExistente);
    }

    @Test
    void testactualizarConsultaNoExiste() {
        Consulta datosaCambiar = new Consulta();
        datosaCambiar.setMotivoConsulta("Vacunación");

        when(consultaRepository.findById(1L)).thenReturn(Optional.empty());

        Consulta resultado = consultaService.actualizarConsulta(1L, datosaCambiar);

        assertThat(resultado).isNull();

        verify(consultaRepository).findById(1L);
        verify(consultaRepository, never()).save(any(Consulta.class));
    }

    @Test
    void testactualizarConsultaCamposNulos() {
        Consulta consultaExistente = new Consulta();
        consultaExistente.setIdConsulta(1L);
        consultaExistente.setMotivoConsulta("Control general");
        consultaExistente.setTemperatura("38.0");

        Consulta datosVacios = new Consulta();
        datosVacios.setMotivoConsulta(null);
        datosVacios.setTemperatura(null);
        datosVacios.setPesoActual(null);
        datosVacios.setObservaciones(null);
        datosVacios.setDiagnostico(null);
        datosVacios.setRecetaMedica(null);
        datosVacios.setIndicaciones(null);
        datosVacios.setOrdenExamen(null);

        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consultaExistente));
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Consulta resultado = consultaService.actualizarConsulta(1L, datosVacios);

        assertThat(resultado.getMotivoConsulta()).isEqualTo("Control general");
        assertThat(resultado.getTemperatura()).isEqualTo("38.0");

        verify(consultaRepository).findById(1L);
        verify(consultaRepository).save(consultaExistente);
    }

}
