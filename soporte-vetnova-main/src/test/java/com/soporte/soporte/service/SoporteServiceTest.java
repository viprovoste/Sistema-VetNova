package com.soporte.soporte.service;

import com.soporte.soporte.client.NotificacionClient;
import com.soporte.soporte.client.ReporteClient;
import com.soporte.soporte.model.Soporte;
import com.soporte.soporte.repository.SoporteRepository;
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
class SoporteServiceTest {

    @Mock
    private SoporteRepository soporteRepository;

    @Mock
    private NotificacionClient notificacionClient;

    @Mock
    private ReporteClient reporteClient;

    @InjectMocks
    private SoporteService soporteService;

    private Soporte soporte;

    @BeforeEach
    void setUp() {
        soporte = new Soporte();
        soporte.setId(1L);
        soporte.setAsunto("Problema con login");
        soporte.setDescripcion("No puedo ingresar");
        soporte.setEstado("PENDIENTE");
        soporte.setUsuarioId(10L);
    }

    @Test
    void guardar_debeRetornarSoporteGuardado() {
        // GIVEN
        when(soporteRepository.save(soporte)).thenReturn(soporte);

        // WHEN
        Soporte resultado = soporteService.guardar(soporte);

        // THEN
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(soporteRepository, times(1)).save(soporte);
    }

    @Test
    void guardar_debeSeguirCuandoNotificacionFalla() {
        // GIVEN
        when(soporteRepository.save(soporte)).thenReturn(soporte);
        doThrow(new RuntimeException("Error de conexión"))
                .when(notificacionClient).enviarNotificacion(any());

        // WHEN
        Soporte resultado = soporteService.guardar(soporte);

        // THEN - no lanza excepción, continúa igual
        assertThat(resultado).isNotNull();
    }

    @Test
    void guardar_debeSeguirCuandoReporteFalla() {
        // GIVEN
        when(soporteRepository.save(soporte)).thenReturn(soporte);
        doThrow(new RuntimeException("Error de reporte"))
                .when(reporteClient).registrarDato(any());

        // WHEN
        Soporte resultado = soporteService.guardar(soporte);

        // THEN
        assertThat(resultado).isNotNull();
    }

    @Test
    void listarTodos_debeRetornarListaConElementos() {
        // GIVEN
        when(soporteRepository.findAll()).thenReturn(Arrays.asList(soporte));

        // WHEN
        List<Soporte> resultado = soporteService.listarTodos();

        // THEN
        assertThat(resultado).hasSize(1);
        verify(soporteRepository, times(1)).findAll();
    }

    @Test
    void listarTodos_debeRetornarListaVacia() {
        // GIVEN
        when(soporteRepository.findAll()).thenReturn(List.of());

        // WHEN
        List<Soporte> resultado = soporteService.listarTodos();

        // THEN
        assertThat(resultado).isEmpty();
    }

    @Test
    void buscarPorId_debeRetornarSoporteCuandoExiste() {
        // GIVEN
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporte));

        // WHEN
        Optional<Soporte> resultado = soporteService.buscarPorId(1L);

        // THEN
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    void buscarPorId_debeRetornarVacioCuandoNoExiste() {
        // GIVEN
        when(soporteRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN
        Optional<Soporte> resultado = soporteService.buscarPorId(99L);

        // THEN
        assertThat(resultado).isEmpty();
    }

    @Test
    void eliminar_debeEliminarCuandoExiste() {
        // GIVEN
        when(soporteRepository.existsById(1L)).thenReturn(true);

        // WHEN
        soporteService.eliminar(1L);

        // THEN
        verify(soporteRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN
        when(soporteRepository.existsById(99L)).thenReturn(false);

        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> soporteService.eliminar(99L));

        assertThat(ex.getMessage()).isEqualTo("Soporte no encontrado con ID: 99");
        verify(soporteRepository, never()).deleteById(any());
    }

    @Test
    void existeDuplicado_debeRetornarTrueCuandoExiste() {
        // GIVEN
        when(soporteRepository.existsByUsuarioIdAndAsuntoAndEstadoNot(10L, "Problema con login", "RESUELTO"))
                .thenReturn(true);

        // WHEN
        boolean resultado = soporteService.existeDuplicado(10L, "Problema con login");

        // THEN
        assertThat(resultado).isTrue();
    }

    @Test
    void existeDuplicado_debeRetornarFalseCuandoNoExiste() {
        // GIVEN
        when(soporteRepository.existsByUsuarioIdAndAsuntoAndEstadoNot(10L, "Otro asunto", "RESUELTO"))
                .thenReturn(false);

        // WHEN
        boolean resultado = soporteService.existeDuplicado(10L, "Otro asunto");

        // THEN
        assertThat(resultado).isFalse();
    }
}