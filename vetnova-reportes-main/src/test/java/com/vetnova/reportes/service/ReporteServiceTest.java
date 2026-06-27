package com.vetnova.reportes.service;

import com.vetnova.reportes.model.Reporte;
import com.vetnova.reportes.repository.ReporteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteRepository repository;

    @InjectMocks
    private ReporteService reporteService;

    private Reporte reporte;

    @BeforeEach
    void setUp() {
        reporte = new Reporte();
        reporte.setId(1L);
        reporte.setTotalAtenciones(5);
        reporte.setTotalAlertasGeneradas(3);
        reporte.setRendimientoGlobal(80.0);
    }

    @Test
    void guardarReporte_debeRetornarReporteGuardado() {
        // GIVEN
        when(repository.save(reporte)).thenReturn(reporte);

        // WHEN
        Reporte resultado = reporteService.guardarReporte(reporte);

        // THEN
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(repository, times(1)).save(reporte);
    }

    @Test
    void listarTodos_debeRetornarListaConElementos() {
        // GIVEN
        when(repository.findAll()).thenReturn(Arrays.asList(reporte));

        // WHEN
        List<Reporte> resultado = reporteService.listarTodos();

        // THEN
        assertThat(resultado).hasSize(1);
        verify(repository, times(1)).findAll();
    }

    @Test
    void listarTodos_debeRetornarListaVacia() {
        // GIVEN
        when(repository.findAll()).thenReturn(List.of());

        // WHEN
        List<Reporte> resultado = reporteService.listarTodos();

        // THEN
        assertThat(resultado).isEmpty();
    }

    @Test
    void buscarPorId_debeRetornarReporteCuandoExiste() {
        // GIVEN
        when(repository.findById(1L)).thenReturn(Optional.of(reporte));

        // WHEN
        Optional<Reporte> resultado = reporteService.buscarPorId(1L);

        // THEN
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    void buscarPorId_debeRetornarVacioCuandoNoExiste() {
        // GIVEN
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // WHEN
        Optional<Reporte> resultado = reporteService.buscarPorId(99L);

        // THEN
        assertThat(resultado).isEmpty();
    }

    @Test
    void eliminarReporte_debeEliminarCuandoExiste() {
        // GIVEN
        when(repository.existsById(1L)).thenReturn(true);

        // WHEN
        reporteService.eliminarReporte(1L);

        // THEN
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarReporte_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN
        when(repository.existsById(99L)).thenReturn(false);

        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reporteService.eliminarReporte(99L));

        assertThat(ex.getMessage()).isEqualTo("Reporte no encontrado con ID: 99");
        verify(repository, never()).deleteById(any());
    }
}