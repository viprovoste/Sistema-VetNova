package com.soporte.soporte.service;

import com.soporte.soporte.model.MensajeSoporte;
import com.soporte.soporte.model.Soporte;
import com.soporte.soporte.repository.MensajeSoporteRepository;
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
class MensajeSoporteServiceTest {

    @Mock
    private MensajeSoporteRepository mensajeRepository;

    @Mock
    private SoporteRepository soporteRepository;

    @InjectMocks
    private MensajeSoporteService mensajeSoporteService;

    private Soporte soporte;
    private MensajeSoporte mensaje;

    @BeforeEach
    void setUp() {
        soporte = new Soporte();
        soporte.setId(1L);
        soporte.setAsunto("Problema con login");
        soporte.setEstado("PENDIENTE");
        soporte.setUsuarioId(10L);

        mensaje = new MensajeSoporte();
        mensaje.setId(1L);
        mensaje.setContenido("Necesito ayuda");
        mensaje.setSoporte(soporte);
    }

    @Test
    void agregarMensaje_debeGuardarCuandoSoporteExiste() {
        // GIVEN
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporte));
        when(mensajeRepository.save(mensaje)).thenReturn(mensaje);

        // WHEN
        MensajeSoporte resultado = mensajeSoporteService.agregarMensaje(1L, mensaje);

        // THEN
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(mensajeRepository, times(1)).save(mensaje);
    }

    @Test
    void agregarMensaje_debeLanzarExcepcionCuandoSoporteNoExiste() {
        // GIVEN
        when(soporteRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> mensajeSoporteService.agregarMensaje(99L, mensaje));

        assertThat(ex.getMessage()).isEqualTo("Soporte no encontrado con ID: 99");
        verify(mensajeRepository, never()).save(any());
    }

    @Test
    void listarMensajes_debeRetornarListaConElementos() {
        // GIVEN
        when(mensajeRepository.findBySoporteId(1L)).thenReturn(Arrays.asList(mensaje));

        // WHEN
        List<MensajeSoporte> resultado = mensajeSoporteService.listarMensajes(1L);

        // THEN
        assertThat(resultado).hasSize(1);
        verify(mensajeRepository, times(1)).findBySoporteId(1L);
    }

    @Test
    void listarMensajes_debeRetornarListaVacia() {
        // GIVEN
        when(mensajeRepository.findBySoporteId(99L)).thenReturn(List.of());

        // WHEN
        List<MensajeSoporte> resultado = mensajeSoporteService.listarMensajes(99L);

        // THEN
        assertThat(resultado).isEmpty();
    }

    @Test
    void eliminarMensaje_debeEliminarCuandoExiste() {
        // GIVEN
        when(mensajeRepository.existsById(1L)).thenReturn(true);

        // WHEN
        mensajeSoporteService.eliminarMensaje(1L);

        // THEN
        verify(mensajeRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarMensaje_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN
        when(mensajeRepository.existsById(99L)).thenReturn(false);

        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> mensajeSoporteService.eliminarMensaje(99L));

        assertThat(ex.getMessage()).isEqualTo("Mensaje no encontrado con ID: 99");
        verify(mensajeRepository, never()).deleteById(any());
    }
}