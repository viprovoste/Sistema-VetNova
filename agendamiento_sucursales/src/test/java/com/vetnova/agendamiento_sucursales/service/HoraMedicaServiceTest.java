package com.vetnova.agendamiento_sucursales.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import com.vetnova.agendamiento_sucursales.model.Box;
import com.vetnova.agendamiento_sucursales.model.ClienteDTO;
import com.vetnova.agendamiento_sucursales.model.EstadoBox;
import com.vetnova.agendamiento_sucursales.model.EstadoHora;
import com.vetnova.agendamiento_sucursales.model.HoraMedica;
import com.vetnova.agendamiento_sucursales.model.HoraMedicaDTO;
import com.vetnova.agendamiento_sucursales.model.MascotaDTO;
import com.vetnova.agendamiento_sucursales.model.Sucursal;
import com.vetnova.agendamiento_sucursales.repository.BoxRepository;
import com.vetnova.agendamiento_sucursales.repository.HoraMedicaRepository;

public class HoraMedicaServiceTest {

    @Mock
    private HoraMedicaRepository horaMedicaRepository;

    @Mock
    private BoxRepository boxRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HoraMedicaService horaMedicaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private HoraMedicaDTO crearDtoCompleto() {
        HoraMedicaDTO dto = new HoraMedicaDTO();
        dto.setIdMascota(1L);
        dto.setIdCliente(1L);
        dto.setMotivoConsulta("Control general");
        dto.setFechaHora(LocalDateTime.of(2026, 1, 15, 10, 0));
        dto.setNombreVeterinario("Dr. House");
        dto.setIdBox(1L);
        return dto;
    }

    private MascotaDTO crearMascota() {
        MascotaDTO mascota = new MascotaDTO();
        mascota.setNombre("Firulais");
        mascota.setEspecie("Perro");
        return mascota;
    }

    private ClienteDTO crearCliente() {
        ClienteDTO cliente = new ClienteDTO();
        cliente.setNombres("Juan Carlos");
        cliente.setApellidos("Pérez González");
        cliente.setRut("12345678-9");
        return cliente;
    }

    private Box crearBox() {
        Sucursal sucursal = new Sucursal();
        sucursal.setIdSucursal(1L);
        return new Box(1L, "Box-1", EstadoBox.DISPONIBLE, sucursal);
    }

    @Test
    void testguardarHoraMedica() {
        HoraMedicaDTO dto = crearDtoCompleto();

        HoraMedica horaGuardada = new HoraMedica();
        horaGuardada.setIdHoraMedica(1L);
        horaGuardada.setNombreMascota("Firulais");

        when(restTemplate.getForObject(contains("/api/mascotas/"), eq(MascotaDTO.class))).thenReturn(crearMascota());
        when(restTemplate.getForObject(contains("/api/clientes/"), eq(ClienteDTO.class))).thenReturn(crearCliente());
        when(boxRepository.findById(1L)).thenReturn(Optional.of(crearBox()));
        when(horaMedicaRepository.save(any(HoraMedica.class))).thenReturn(horaGuardada);

        HoraMedica resultado = horaMedicaService.guardarHoraMedica(dto);

        assertThat(resultado).isEqualTo(horaGuardada);
        assertThat(resultado.getNombreMascota()).isEqualTo("Firulais");

        verify(horaMedicaRepository).save(any(HoraMedica.class));
    }

    @Test
    void testguardarHoraMedicaMascotaNula() {
        HoraMedicaDTO dto = crearDtoCompleto();

        when(restTemplate.getForObject(contains("/api/mascotas/"), eq(MascotaDTO.class))).thenReturn(null);
        when(restTemplate.getForObject(contains("/api/clientes/"), eq(ClienteDTO.class))).thenReturn(crearCliente());
        when(boxRepository.findById(1L)).thenReturn(Optional.of(crearBox()));

        HoraMedica resultado = horaMedicaService.guardarHoraMedica(dto);

        assertThat(resultado).isNull();

        verify(horaMedicaRepository, never()).save(any(HoraMedica.class));
    }

    @Test
    void testguardarHoraMedicaClienteNulo() {
        HoraMedicaDTO dto = crearDtoCompleto();

        when(restTemplate.getForObject(contains("/api/mascotas/"), eq(MascotaDTO.class))).thenReturn(crearMascota());
        when(restTemplate.getForObject(contains("/api/clientes/"), eq(ClienteDTO.class))).thenReturn(null);
        when(boxRepository.findById(1L)).thenReturn(Optional.of(crearBox()));

        HoraMedica resultado = horaMedicaService.guardarHoraMedica(dto);

        assertThat(resultado).isNull();

        verify(horaMedicaRepository, never()).save(any(HoraMedica.class));
    }

    @Test
    void testguardarHoraMedicaBoxNulo() {
        HoraMedicaDTO dto = crearDtoCompleto();

        when(restTemplate.getForObject(contains("/api/mascotas/"), eq(MascotaDTO.class))).thenReturn(crearMascota());
        when(restTemplate.getForObject(contains("/api/clientes/"), eq(ClienteDTO.class))).thenReturn(crearCliente());
        when(boxRepository.findById(1L)).thenReturn(Optional.empty());

        HoraMedica resultado = horaMedicaService.guardarHoraMedica(dto);

        assertThat(resultado).isNull();

        verify(horaMedicaRepository, never()).save(any(HoraMedica.class));
    }

    @Test
    void testlistarPorNombreMascota() {
        HoraMedica hora = new HoraMedica();
        hora.setNombreMascota("Firulais");

        when(horaMedicaRepository.findByNombreMascota("Firulais")).thenReturn(List.of(hora));

        List<HoraMedica> resultado = horaMedicaService.listarPorNombreMascota("Firulais");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombreMascota()).isEqualTo("Firulais");

        verify(horaMedicaRepository).findByNombreMascota("Firulais");
    }

    @Test
    void testlistarPorNombreDueño() {
        HoraMedica hora = new HoraMedica();
        hora.setNombreDueño("Juan Carlos");

        when(horaMedicaRepository.findByNombreDueño("Juan Carlos")).thenReturn(List.of(hora));

        List<HoraMedica> resultado = horaMedicaService.listarPorNombreDueño("Juan Carlos");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombreDueño()).isEqualTo("Juan Carlos");

        verify(horaMedicaRepository).findByNombreDueño("Juan Carlos");
    }

    @Test
    void testlistarPorRutDueño() {
        HoraMedica hora = new HoraMedica();
        hora.setRutDueño("12345678-9");

        when(horaMedicaRepository.findByRutDueño("12345678-9")).thenReturn(List.of(hora));

        List<HoraMedica> resultado = horaMedicaService.listarPorRutDueño("12345678-9");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRutDueño()).isEqualTo("12345678-9");

        verify(horaMedicaRepository).findByRutDueño("12345678-9");
    }

    @Test
    void testlistarPorVeterinario() {
        HoraMedica hora = new HoraMedica();
        hora.setNombreVeterinario("Dr. House");

        when(horaMedicaRepository.findByNombreVeterinario("Dr. House")).thenReturn(List.of(hora));

        List<HoraMedica> resultado = horaMedicaService.listarPorVeterinario("Dr. House");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombreVeterinario()).isEqualTo("Dr. House");

        verify(horaMedicaRepository).findByNombreVeterinario("Dr. House");
    }

    @Test
    void testactualizarHoraMedica() {
        HoraMedica horaExistente = new HoraMedica();
        horaExistente.setIdHoraMedica(1L);
        horaExistente.setMotivoConsulta("Control general");

        HoraMedicaDTO dto = new HoraMedicaDTO();
        dto.setFechaHora(LocalDateTime.of(2026, 2, 20, 15, 0));
        dto.setMotivoConsulta("Vacunación");
        dto.setNombreVeterinario("Dr. Wilson");
        dto.setIdBox(2L);

        Box nuevoBox = crearBox();

        when(horaMedicaRepository.findById(1L)).thenReturn(Optional.of(horaExistente));
        when(boxRepository.findById(2L)).thenReturn(Optional.of(nuevoBox));
        when(horaMedicaRepository.save(any(HoraMedica.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        HoraMedica resultado = horaMedicaService.actualizarHoraMedica(1L, dto);

        assertThat(resultado.getMotivoConsulta()).isEqualTo("Vacunación");
        assertThat(resultado.getNombreVeterinario()).isEqualTo("Dr. Wilson");
        assertThat(resultado.getFechaHora()).isEqualTo(LocalDateTime.of(2026, 2, 20, 15, 0));
        assertThat(resultado.getBox()).isEqualTo(nuevoBox);

        verify(horaMedicaRepository).findById(1L);
        verify(horaMedicaRepository).save(horaExistente);
    }

    @Test
    void testactualizarHoraMedicaBoxNoExiste() {
        HoraMedica horaExistente = new HoraMedica();
        horaExistente.setIdHoraMedica(1L);
        Box boxOriginal = crearBox();
        horaExistente.setBox(boxOriginal);

        HoraMedicaDTO dto = new HoraMedicaDTO();
        dto.setIdBox(99L);

        when(horaMedicaRepository.findById(1L)).thenReturn(Optional.of(horaExistente));
        when(boxRepository.findById(99L)).thenReturn(Optional.empty());
        when(horaMedicaRepository.save(any(HoraMedica.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        HoraMedica resultado = horaMedicaService.actualizarHoraMedica(1L, dto);

        assertThat(resultado.getBox()).isEqualTo(boxOriginal);

        verify(boxRepository).findById(99L);
        verify(horaMedicaRepository).save(horaExistente);
    }

    @Test
    void testactualizarHoraMedicaCamposNulos() {
        HoraMedica horaExistente = new HoraMedica();
        horaExistente.setIdHoraMedica(1L);
        horaExistente.setMotivoConsulta("Control general");
        horaExistente.setNombreVeterinario("Dr. House");

        HoraMedicaDTO dto = new HoraMedicaDTO();
        dto.setFechaHora(null);
        dto.setMotivoConsulta(null);
        dto.setNombreVeterinario(null);
        dto.setIdBox(null);

        when(horaMedicaRepository.findById(1L)).thenReturn(Optional.of(horaExistente));
        when(horaMedicaRepository.save(any(HoraMedica.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        HoraMedica resultado = horaMedicaService.actualizarHoraMedica(1L, dto);

        assertThat(resultado.getMotivoConsulta()).isEqualTo("Control general");
        assertThat(resultado.getNombreVeterinario()).isEqualTo("Dr. House");

        verify(horaMedicaRepository).findById(1L);
        verify(horaMedicaRepository).save(horaExistente);
        verify(boxRepository, never()).findById(any());
    }

    @Test
    void testactualizarHoraMedicaNoExiste() {
        HoraMedicaDTO dto = new HoraMedicaDTO();
        dto.setMotivoConsulta("Vacunación");

        when(horaMedicaRepository.findById(1L)).thenReturn(Optional.empty());

        HoraMedica resultado = horaMedicaService.actualizarHoraMedica(1L, dto);

        assertThat(resultado).isNull();

        verify(horaMedicaRepository).findById(1L);
        verify(horaMedicaRepository, never()).save(any(HoraMedica.class));
    }

    @Test
    void testcancelarHoraMedica() {
        HoraMedica horaExistente = new HoraMedica();
        horaExistente.setIdHoraMedica(1L);
        horaExistente.setEstado(EstadoHora.CONFIRMADA);

        when(horaMedicaRepository.findById(1L)).thenReturn(Optional.of(horaExistente));
        when(horaMedicaRepository.save(any(HoraMedica.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        HoraMedica resultado = horaMedicaService.cancelarHoraMedica(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoHora.CANCELADA);

        verify(horaMedicaRepository).findById(1L);
        verify(horaMedicaRepository).save(horaExistente);
    }

    @Test
    void testcancelarHoraMedicaNoExiste() {
        when(horaMedicaRepository.findById(1L)).thenReturn(Optional.empty());

        HoraMedica resultado = horaMedicaService.cancelarHoraMedica(1L);

        assertThat(resultado).isNull();

        verify(horaMedicaRepository).findById(1L);
        verify(horaMedicaRepository, never()).save(any(HoraMedica.class));
    }

}