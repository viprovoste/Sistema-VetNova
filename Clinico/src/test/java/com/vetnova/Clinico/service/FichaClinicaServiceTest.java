package com.vetnova.Clinico.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.web.client.RestTemplate;

import com.vetnova.Clinico.model.ClienteDTO;
import com.vetnova.Clinico.model.FichaClinica;
import com.vetnova.Clinico.model.FichaClinicaDTO;
import com.vetnova.Clinico.model.MascotaDTO;
import com.vetnova.Clinico.repository.FichaClinicaRepository;

public class FichaClinicaServiceTest {

    @Mock
    private FichaClinicaRepository fichaClinicaRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FichaClinicaService fichaClinicaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testguardarFichaClinica() {
        FichaClinicaDTO dto = new FichaClinicaDTO();
        dto.setIdMascota(1L);
        dto.setIdCliente(1L);

        MascotaDTO mascota = new MascotaDTO();
        mascota.setNombre("Firulais");
        mascota.setEspecie("Perro");
        mascota.setPeso("20");

        ClienteDTO cliente = new ClienteDTO();
        cliente.setNombres("Juan Carlos");
        cliente.setApellidos("Pérez González");
        cliente.setRut("12345678-9");

        FichaClinica fichaGuardada = new FichaClinica();
        fichaGuardada.setIdFicha(1L);
        fichaGuardada.setNombreMascota("Firulais");

        when(restTemplate.getForObject(contains("/api/mascotas/"), eq(MascotaDTO.class))).thenReturn(mascota);
        when(restTemplate.getForObject(contains("/api/clientes/"), eq(ClienteDTO.class))).thenReturn(cliente);
        when(fichaClinicaRepository.save(any(FichaClinica.class))).thenReturn(fichaGuardada);

        FichaClinica resultado = fichaClinicaService.guardarFichaClinica(dto);

        assertThat(resultado).isEqualTo(fichaGuardada);
        assertThat(resultado.getNombreMascota()).isEqualTo("Firulais");

        verify(fichaClinicaRepository).save(any(FichaClinica.class));
    }

    @Test
    void testguardarFichaClinicaMascotaNula() {
        FichaClinicaDTO dto = new FichaClinicaDTO();
        dto.setIdMascota(1L);
        dto.setIdCliente(1L);

        ClienteDTO cliente = new ClienteDTO();
        cliente.setNombres("Juan Carlos");

        when(restTemplate.getForObject(contains("/api/mascotas/"), eq(MascotaDTO.class))).thenReturn(null);
        when(restTemplate.getForObject(contains("/api/clientes/"), eq(ClienteDTO.class))).thenReturn(cliente);

        FichaClinica resultado = fichaClinicaService.guardarFichaClinica(dto);

        assertThat(resultado).isNull();

        verify(fichaClinicaRepository, never()).save(any(FichaClinica.class));
    }

    @Test
    void testguardarFichaClinicaClienteNulo() {
        FichaClinicaDTO dto = new FichaClinicaDTO();
        dto.setIdMascota(1L);
        dto.setIdCliente(1L);

        MascotaDTO mascota = new MascotaDTO();
        mascota.setNombre("Firulais");

        when(restTemplate.getForObject(contains("/api/mascotas/"), eq(MascotaDTO.class))).thenReturn(mascota);
        when(restTemplate.getForObject(contains("/api/clientes/"), eq(ClienteDTO.class))).thenReturn(null);

        FichaClinica resultado = fichaClinicaService.guardarFichaClinica(dto);

        assertThat(resultado).isNull();

        verify(fichaClinicaRepository, never()).save(any(FichaClinica.class));
    }

    @Test
    void testlistarFichasClinicas() {
        FichaClinica ficha = new FichaClinica();
        ficha.setIdFicha(1L);
        ficha.setNombreMascota("Firulais");

        List<FichaClinica> fichas = new ArrayList<>();
        fichas.add(ficha);

        when(fichaClinicaRepository.findAll()).thenReturn(fichas);

        List<FichaClinica> resultado = fichaClinicaService.listarFichasClinicas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado).contains(ficha);

        verify(fichaClinicaRepository).findAll();
    }

    @Test
    void testfindById() {
        FichaClinica ficha = new FichaClinica();
        ficha.setIdFicha(1L);
        ficha.setNombreMascota("Firulais");

        when(fichaClinicaRepository.findById(1L)).thenReturn(Optional.of(ficha));

        Optional<FichaClinica> resultado = fichaClinicaService.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombreMascota()).isEqualTo("Firulais");

        verify(fichaClinicaRepository).findById(1L);
    }

    @Test
    void testfindByNombreMascota() {
        FichaClinica ficha = new FichaClinica();
        ficha.setNombreMascota("Firulais");

        when(fichaClinicaRepository.findByNombreMascota("Firulais")).thenReturn(List.of(ficha));

        List<FichaClinica> resultado = fichaClinicaService.findByNombreMascota("Firulais");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombreMascota()).isEqualTo("Firulais");

        verify(fichaClinicaRepository).findByNombreMascota("Firulais");
    }

    @Test
    void testfindByNombreDueño() {
        FichaClinica ficha = new FichaClinica();
        ficha.setNombreDueño("Juan Carlos");

        when(fichaClinicaRepository.findByNombreDueño("Juan Carlos")).thenReturn(List.of(ficha));

        List<FichaClinica> resultado = fichaClinicaService.findByNombreDueño("Juan Carlos");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombreDueño()).isEqualTo("Juan Carlos");

        verify(fichaClinicaRepository).findByNombreDueño("Juan Carlos");
    }

    @Test
    void testfindByRutDueño() {
        FichaClinica ficha = new FichaClinica();
        ficha.setRutDueño("12345678-9");

        when(fichaClinicaRepository.findByRutDueño("12345678-9")).thenReturn(List.of(ficha));

        List<FichaClinica> resultado = fichaClinicaService.findByRutDueño("12345678-9");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRutDueño()).isEqualTo("12345678-9");

        verify(fichaClinicaRepository).findByRutDueño("12345678-9");
    }

    @Test
    void testactualizarFichaClinica() {
        FichaClinica fichaExistente = new FichaClinica();
        fichaExistente.setIdFicha(1L);
        fichaExistente.setAlergias("Sin alergias");
        fichaExistente.setPeso("20");

        FichaClinica datosaCambiar = new FichaClinica();
        datosaCambiar.setAlergias("Polen");
        datosaCambiar.setPeso("22");

        when(fichaClinicaRepository.findById(1L)).thenReturn(Optional.of(fichaExistente));
        when(fichaClinicaRepository.save(any(FichaClinica.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        FichaClinica resultado = fichaClinicaService.actualizarFichaClinica(1L, datosaCambiar);

        assertThat(resultado.getAlergias()).isEqualTo("Polen");
        assertThat(resultado.getPeso()).isEqualTo("22");

        verify(fichaClinicaRepository).findById(1L);
        verify(fichaClinicaRepository).save(fichaExistente);
    }

    @Test
    void testactualizarFichaClinicaNoExiste() {
        FichaClinica datosaCambiar = new FichaClinica();
        datosaCambiar.setAlergias("Polen");

        when(fichaClinicaRepository.findById(1L)).thenReturn(Optional.empty());

        FichaClinica resultado = fichaClinicaService.actualizarFichaClinica(1L, datosaCambiar);

        assertThat(resultado).isNull();

        verify(fichaClinicaRepository).findById(1L);
        verify(fichaClinicaRepository, never()).save(any(FichaClinica.class));
    }

    @Test
    void testactualizarFichaClinicaCamposNulos() {
        FichaClinica fichaExistente = new FichaClinica();
        fichaExistente.setIdFicha(1L);
        fichaExistente.setAlergias("Sin alergias");
        fichaExistente.setPeso("20");

        FichaClinica datosVacios = new FichaClinica();
        datosVacios.setAlergias(null);
        datosVacios.setPeso(null);

        when(fichaClinicaRepository.findById(1L)).thenReturn(Optional.of(fichaExistente));
        when(fichaClinicaRepository.save(any(FichaClinica.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        FichaClinica resultado = fichaClinicaService.actualizarFichaClinica(1L, datosVacios);

        assertThat(resultado.getAlergias()).isEqualTo("Sin alergias");
        assertThat(resultado.getPeso()).isEqualTo("20");

        verify(fichaClinicaRepository).findById(1L);
        verify(fichaClinicaRepository).save(fichaExistente);
    }

    @Test
    void testeliminarFichaClinica() {
        when(fichaClinicaRepository.existsById(1L)).thenReturn(true);

        boolean resultado = fichaClinicaService.eliminarFichaClinica(1L);

        assertThat(resultado).isTrue();

        verify(fichaClinicaRepository).deleteById(1L);
    }

    @Test
    void testeliminarFichaClinicaNoExiste() {
        when(fichaClinicaRepository.existsById(1L)).thenReturn(false);

        boolean resultado = fichaClinicaService.eliminarFichaClinica(1L);

        assertThat(resultado).isFalse();

        verify(fichaClinicaRepository).existsById(1L);
        verify(fichaClinicaRepository, never()).deleteById(1L);
    }

}
