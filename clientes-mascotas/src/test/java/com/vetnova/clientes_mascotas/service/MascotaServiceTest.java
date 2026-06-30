package com.vetnova.clientes_mascotas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vetnova.clientes_mascotas.model.Cliente;
import com.vetnova.clientes_mascotas.model.Mascota;
import com.vetnova.clientes_mascotas.model.MascotaDTO;
import com.vetnova.clientes_mascotas.repository.ClienteRepository;
import com.vetnova.clientes_mascotas.repository.MascotaRepository;

public class MascotaServiceTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private MascotaService mascotaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGuardarMascota() {
        MascotaDTO dto = new MascotaDTO();
        dto.setNombre("Firulais");
        dto.setEspecie("Perro");
        dto.setPeso("20");
        dto.setIdCliente(1L);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);

        Mascota mascotaGuardada = new Mascota(1L, "Firulais", "Perro", "20", null, cliente);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascotaGuardada);

        Mascota resultado = mascotaService.guardarMascota(dto);

        assertThat(resultado).isEqualTo(mascotaGuardada);
        assertThat(resultado.getIdCMascota()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Firulais");

        verify(clienteRepository).findById(1L);
        verify(mascotaRepository).save(any(Mascota.class));

    }

    @Test
    void testGuardarMascotaClienteNoExiste() {
        MascotaDTO dto = new MascotaDTO();
        dto.setNombre("Firulais");
        dto.setEspecie("Perro");
        dto.setPeso("20");
        dto.setIdCliente(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mascotaService.guardarMascota(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente no encontrado");

        verify(clienteRepository).findById(1L);
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    void testlistarMascotas() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);

        Mascota mascota = new Mascota(1L, "Firulais", "Perro", "20", null, cliente);
        List<Mascota> mascotas = new ArrayList<>();
        mascotas.add(mascota);

        when(mascotaRepository.findAll()).thenReturn(mascotas);

        List<Mascota> resultado = mascotaService.listarMascotas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado).contains(mascota);

        verify(mascotaRepository).findAll();
    }

    @Test
    void testfindById() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);

        Mascota mascota = new Mascota(1L, "Firulais", "Perro", "20", null, cliente);

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));

        Optional<Mascota> resultado = mascotaService.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Firulais");

        verify(mascotaRepository).findById(1L);
    }
    
    @Test
    void testfindByNombre() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);

        Mascota mascota = new Mascota(1L, "Firulais", "Perro", "20", null, cliente);

        when(mascotaRepository.findByNombre("Firulais")).thenReturn(List.of(mascota));

        List<Mascota> resultado = mascotaService.findByNombre("Firulais");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Firulais");

        verify(mascotaRepository).findByNombre("Firulais");

    }

    @Test
    void testactualizarMascota() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);

        Mascota mascotaExistente = new Mascota(1L, "Firulais", "Perro", "20", null, cliente);
        Mascota mascotaActualizado = new Mascota(1L, "Pelusa", "Gato", "8", null, cliente);

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascotaExistente));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Mascota resultado = mascotaService.actualizarMascota(1L, mascotaActualizado);

        assertThat(resultado.getNombre()).isEqualTo("Pelusa");
        assertThat(resultado.getEspecie()).isEqualTo("Gato");
        assertThat(resultado.getPeso()).isEqualTo("8");

        verify(mascotaRepository).findById(1L);
        verify(mascotaRepository).save(mascotaExistente);
    }

    @Test
    void testactualizarMascotaNoExiste() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);

        Mascota mascotaActualizado = new Mascota(1L, "Pelusa", "Gato", "8", null, cliente);

        when(mascotaRepository.findById(1L)).thenReturn(Optional.empty());

        Mascota resultado = mascotaService.actualizarMascota(1L, mascotaActualizado);

        assertThat(resultado).isNull();

        verify(mascotaRepository).findById(1L);
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    void testactualizarMascotaCamposNulos() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);

        Mascota mascotaExistente = new Mascota(1L, "Firulais", "Perro", "20", null, cliente);
        Mascota datosVacios = new Mascota(1L, null, null, null, null, cliente);

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascotaExistente));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Mascota resultado = mascotaService.actualizarMascota(1L, datosVacios);

        assertThat(resultado.getNombre()).isEqualTo("Firulais");
        assertThat(resultado.getEspecie()).isEqualTo("Perro");
        assertThat(resultado.getPeso()).isEqualTo("20");

        verify(mascotaRepository).findById(1L);
        verify(mascotaRepository).save(mascotaExistente);
    }

    @Test
    void testeliminarMascota() {
        when(mascotaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(mascotaRepository).deleteById(1L);

        mascotaService.eliminarMascota(1L);

        verify(mascotaRepository).deleteById(1L);
    }

    @Test
    void testeliminarMascotaNoExiste() {
        when(mascotaRepository.existsById(1L)).thenReturn(false);

        boolean resultado = mascotaService.eliminarMascota(1L);

        assertThat(resultado).isFalse();

        verify(mascotaRepository).existsById(1L);
        verify(mascotaRepository, never()).deleteById(1L);
    }

}
