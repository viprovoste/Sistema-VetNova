package com.vetnova.agendamiento_sucursales.service;

import static org.assertj.core.api.Assertions.assertThat;
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

import com.vetnova.agendamiento_sucursales.model.Sucursal;
import com.vetnova.agendamiento_sucursales.repository.SucursalRepository;

public class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private SucursalService sucursalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testlistarSucursales() {
        Sucursal sucursal = new Sucursal();
        sucursal.setIdSucursal(1L);
        sucursal.setNombre("VetNova Centro");

        List<Sucursal> sucursales = new ArrayList<>();
        sucursales.add(sucursal);

        when(sucursalRepository.findAll()).thenReturn(sucursales);

        List<Sucursal> resultado = sucursalService.listarSucursales();

        assertThat(resultado).hasSize(1);
        assertThat(resultado).contains(sucursal);

        verify(sucursalRepository).findAll();
    }

    @Test
    void testfindById() {
        Sucursal sucursal = new Sucursal();
        sucursal.setIdSucursal(1L);
        sucursal.setNombre("VetNova Centro");

        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));

        Optional<Sucursal> resultado = sucursalService.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("VetNova Centro");

        verify(sucursalRepository).findById(1L);
    }

}