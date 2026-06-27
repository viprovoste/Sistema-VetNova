package com.vetnova.reportes.controller;

import com.vetnova.reportes.dto.SoporteDTO;
import com.vetnova.reportes.model.Reporte;
import com.vetnova.reportes.service.IReporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

    @Mock
    private IReporteService service;

    @InjectMocks
    private ReporteController reporteController;

    private Reporte reporte;
    private SoporteDTO dto;

    @BeforeEach
    void setUp() {
        reporte = new Reporte();
        reporte.setId(1L);
        reporte.setTotalAtenciones(1);
        reporte.setTotalAlertasGeneradas(1);
        reporte.setRendimientoGlobal(100.0);

        dto = new SoporteDTO();
        dto.setUsuarioId(10L);
        dto.setEstado("RESUELTO");
        dto.setAsunto("Problema login");
        dto.setDescripcion("No puedo ingresar");
    }

    @Test
    void crearReporte_debeRetornar201CuandoEstadoEsResuelto() {
        // GIVEN
        when(service.guardarReporte(any(Reporte.class))).thenReturn(reporte);

        // WHEN
        ResponseEntity<Reporte> response = reporteController.crearReporte(dto);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody().getRendimientoGlobal()).isEqualTo(100.0);
    }

    @Test
    void crearReporte_debeRetornar201CuandoEstadoNOEsResuelto() {
        // GIVEN
        dto.setEstado("PENDIENTE");
        when(service.guardarReporte(any(Reporte.class))).thenReturn(reporte);

        // WHEN
        ResponseEntity<Reporte> response = reporteController.crearReporte(dto);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(201);
    }

    @Test
    void crearReporte_debeRetornar201CuandoUsuarioIdEsNull() {
        // GIVEN
        dto.setUsuarioId(null);
        when(service.guardarReporte(any(Reporte.class))).thenReturn(reporte);

        // WHEN
        ResponseEntity<Reporte> response = reporteController.crearReporte(dto);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(201);
    }

    @Test
    void obtenerTodos_debeRetornar200ConLista() {
        // GIVEN
        when(service.listarTodos()).thenReturn(Arrays.asList(reporte));

        // WHEN
        ResponseEntity<List<Reporte>> response = reporteController.obtenerTodos();

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void obtenerPorId_debeRetornar200CuandoExiste() {
        // GIVEN
        when(service.buscarPorId(1L)).thenReturn(Optional.of(reporte));

        // WHEN
        ResponseEntity<Reporte> response = reporteController.obtenerPorId(1L);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void obtenerPorId_debeRetornar404CuandoNoExiste() {
        // GIVEN
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        // WHEN
        ResponseEntity<Reporte> response = reporteController.obtenerPorId(99L);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void actualizar_debeRetornar200CuandoExiste() {
        // GIVEN
        when(service.buscarPorId(1L)).thenReturn(Optional.of(reporte));
        when(service.guardarReporte(any(Reporte.class))).thenReturn(reporte);

        // WHEN
        ResponseEntity<Reporte> response = reporteController.actualizar(1L, reporte);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void actualizar_debeRetornar404CuandoNoExiste() {
        // GIVEN
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        // WHEN
        ResponseEntity<Reporte> response = reporteController.actualizar(99L, reporte);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void eliminar_debeRetornar204() {
        // WHEN
        ResponseEntity<Void> response = reporteController.eliminar(1L);

        // THEN
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }
}