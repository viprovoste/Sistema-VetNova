package com.vetnova.agendamiento_sucursales.service;

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

import com.vetnova.agendamiento_sucursales.model.Box;
import com.vetnova.agendamiento_sucursales.model.EstadoBox;
import com.vetnova.agendamiento_sucursales.model.Sucursal;
import com.vetnova.agendamiento_sucursales.repository.BoxRepository;

public class BoxServiceTest {

    @Mock
    private BoxRepository boxRepository;

    @InjectMocks
    private BoxService boxService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testlistarBoxesPorSucursal() {
        Sucursal sucursal = new Sucursal();
        sucursal.setIdSucursal(1L);

        Box box = new Box(1L, "Box-1", EstadoBox.DISPONIBLE, sucursal);
        List<Box> boxes = new ArrayList<>();
        boxes.add(box);

        when(boxRepository.findBySucursalIdSucursal(1L)).thenReturn(boxes);

        List<Box> resultado = boxService.listarBoxesPorSucursal(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado).contains(box);

        verify(boxRepository).findBySucursalIdSucursal(1L);
    }

    @Test
    void testlistarBoxesPorEstado() {
        Sucursal sucursal = new Sucursal();
        sucursal.setIdSucursal(1L);

        Box box = new Box(1L, "Box-1", EstadoBox.DISPONIBLE, sucursal);

        when(boxRepository.findBySucursalIdSucursalAndEstado(1L, EstadoBox.DISPONIBLE)).thenReturn(List.of(box));

        List<Box> resultado = boxService.listarBoxesPorEstado(1L, EstadoBox.DISPONIBLE);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoBox.DISPONIBLE);

        verify(boxRepository).findBySucursalIdSucursalAndEstado(1L, EstadoBox.DISPONIBLE);
    }

    @Test
    void testactualizarEstadoBox() {
        Sucursal sucursal = new Sucursal();
        sucursal.setIdSucursal(1L);

        Box boxExistente = new Box(1L, "Box-1", EstadoBox.DISPONIBLE, sucursal);

        when(boxRepository.findById(1L)).thenReturn(Optional.of(boxExistente));
        when(boxRepository.save(any(Box.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Box resultado = boxService.actualizarEstadoBox(1L, EstadoBox.OCUPADO);

        assertThat(resultado.getEstado()).isEqualTo(EstadoBox.OCUPADO);

        verify(boxRepository).findById(1L);
        verify(boxRepository).save(boxExistente);
    }

    @Test
    void testactualizarEstadoBoxNoExiste() {
        when(boxRepository.findById(1L)).thenReturn(Optional.empty());

        Box resultado = boxService.actualizarEstadoBox(1L, EstadoBox.OCUPADO);

        assertThat(resultado).isNull();

        verify(boxRepository).findById(1L);
        verify(boxRepository, never()).save(any(Box.class));
    }

}