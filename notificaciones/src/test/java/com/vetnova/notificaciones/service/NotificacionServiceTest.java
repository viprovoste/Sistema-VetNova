package com.vetnova.notificaciones.service;

import com.vetnova.notificaciones.model.Notificacion;
import com.vetnova.notificaciones.repository.NotificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    private Notificacion notificacion;

    @BeforeEach
    void setUp() {
        notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setTipo("EMAIL");
        notificacion.setDestinatario("usuario-1");
        notificacion.setAsunto("Recordatorio de cita");
        notificacion.setMensaje("Tiene una cita mañana");
        notificacion.setEstado("PENDIENTE");
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setIdCita(100L);
    }

    @Test
    void guardar_debeRetornarNotificacionGuardada() {
        // GIVEN
        when(notificacionRepository.save(notificacion)).thenReturn(notificacion);

        // WHEN
        Notificacion resultado = notificacionService.guardar(notificacion);

        // THEN
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(notificacionRepository, times(1)).save(notificacion);
    }

    @Test
    void listarTodas_debeRetornarListaConElementos() {
        // GIVEN
        Notificacion otra = new Notificacion();
        otra.setId(2L);
        when(notificacionRepository.findAll()).thenReturn(Arrays.asList(notificacion, otra));

        // WHEN
        List<Notificacion> resultado = notificacionService.listarTodas();

        // THEN
        assertThat(resultado).hasSize(2);
        verify(notificacionRepository, times(1)).findAll();
    }

    @Test
    void listarTodas_debeRetornarListaVacia() {
        // GIVEN
        when(notificacionRepository.findAll()).thenReturn(List.of());

        // WHEN
        List<Notificacion> resultado = notificacionService.listarTodas();

        // THEN
        assertThat(resultado).isEmpty();
    }

    @Test
    void buscarPorId_debeRetornarNotificacionCuandoExiste() {
        // GIVEN
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));

        // WHEN
        Optional<Notificacion> resultado = notificacionService.buscarPorId(1L);

        // THEN
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    void buscarPorId_debeRetornarVacioCuandoNoExiste() {
        // GIVEN
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN
        Optional<Notificacion> resultado = notificacionService.buscarPorId(99L);

        // THEN
        assertThat(resultado).isEmpty();
    }

    @Test
    void eliminarNotificacion_debeEliminarCuandoExiste() {
        // GIVEN
        when(notificacionRepository.existsById(1L)).thenReturn(true);

        // WHEN
        notificacionService.eliminarNotificacion(1L);

        // THEN
        verify(notificacionRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarNotificacion_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN
        when(notificacionRepository.existsById(99L)).thenReturn(false);

        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificacionService.eliminarNotificacion(99L));

        assertThat(ex.getMessage()).isEqualTo("Notificación no encontrada con ID: 99");
        verify(notificacionRepository, never()).deleteById(any());
    }

    @Test
    void existeDuplicado_debeRetornarTrueCuandoExisteCombinacion() {
        // GIVEN
        when(notificacionRepository.existsByIdCitaAndTipoAndEstado(100L, "EMAIL", "PENDIENTE"))
                .thenReturn(true);

        // WHEN
        boolean resultado = notificacionService.existeDuplicado(100L, "EMAIL", "PENDIENTE");

        // THEN
        assertThat(resultado).isTrue();
    }

    @Test
    void existeDuplicado_debeRetornarFalseCuandoNoExisteCombinacion() {
        // GIVEN
        when(notificacionRepository.existsByIdCitaAndTipoAndEstado(200L, "SMS", "ENVIADO"))
                .thenReturn(false);

        // WHEN
        boolean resultado = notificacionService.existeDuplicado(200L, "SMS", "ENVIADO");

        // THEN
        assertThat(resultado).isFalse();
    }
}