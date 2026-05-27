package com.vetnova.agendamiento_sucursales.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vetnova.agendamiento_sucursales.model.Box;
import com.vetnova.agendamiento_sucursales.model.ClienteDTO;
import com.vetnova.agendamiento_sucursales.model.EstadoHora;
import com.vetnova.agendamiento_sucursales.model.HoraMedica;
import com.vetnova.agendamiento_sucursales.model.HoraMedicaDTO;
import com.vetnova.agendamiento_sucursales.model.MascotaDTO;
import com.vetnova.agendamiento_sucursales.repository.BoxRepository;
import com.vetnova.agendamiento_sucursales.repository.HoraMedicaRepository;

@Service
public class HoraMedicaService {
    @Autowired
    private HoraMedicaRepository horaMedicaRepository;

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private RestTemplate restTemplate;

    public HoraMedica guardarHoraMedica(HoraMedicaDTO dto) {

        String urlMascota = "http://localhost:8082/api/mascotas/" + dto.getIdMascota();
        MascotaDTO mascota = restTemplate.getForObject(urlMascota, MascotaDTO.class);

        String urlCliente = "http://localhost:8082/api/clientes/" + dto.getIdCliente();
        ClienteDTO cliente = restTemplate.getForObject(urlCliente, ClienteDTO.class);

        Box box = boxRepository.findById(dto.getIdBox()).orElse(null);

        if (mascota != null && cliente != null && box != null) {
            HoraMedica hora = new HoraMedica();

            hora.setIdMascota(dto.getIdMascota());
            hora.setIdCliente(dto.getIdCliente());
            hora.setNombreMascota(mascota.getNombre());
            hora.setEspecie(mascota.getEspecie());
            hora.setNombreDueño(cliente.getNombres());
            hora.setApellidoDueño(cliente.getApellidos());
            hora.setRutDueño(cliente.getRut());
            hora.setMotivoConsulta(dto.getMotivoConsulta());
            hora.setFechaHora(dto.getFechaHora());
            hora.setNombreVeterinario(dto.getNombreVeterinario());
            hora.setEstado(EstadoHora.CONFIRMADA); 
            hora.setBox(box);

            return horaMedicaRepository.save(hora);
            }
        return null;
    }

    public List<HoraMedica> listarPorNombreMascota(String nombreMascota) {
        return horaMedicaRepository.findByNombreMascota(nombreMascota);
    }

    public List<HoraMedica> listarPorNombreDueño(String nombreDueño) {
        return horaMedicaRepository.findByNombreDueño(nombreDueño);
    }

    public List<HoraMedica> listarPorRutDueño(String rutDueño) {
        return horaMedicaRepository.findByRutDueño(rutDueño);
    }

    public List<HoraMedica> listarPorVeterinario(String nombreVeterinario) {
        return horaMedicaRepository.findByNombreVeterinario(nombreVeterinario);
    }

    public HoraMedica actualizarHoraMedica(Long id, HoraMedicaDTO dto) {
        Optional<HoraMedica> horaExistente = horaMedicaRepository.findById(id);

        if (horaExistente.isPresent()) {
            HoraMedica hora = horaExistente.get();

            if (dto.getFechaHora() != null) {
                hora.setFechaHora(dto.getFechaHora());
            }
            if (dto.getMotivoConsulta() != null) {
                hora.setMotivoConsulta(dto.getMotivoConsulta());
            }
            if (dto.getNombreVeterinario() != null) {
                hora.setNombreVeterinario(dto.getNombreVeterinario());
            }
            if (dto.getIdBox() != null) {
                Box box = boxRepository.findById(dto.getIdBox()).orElse(null);
                if (box != null) hora.setBox(box);
            }
            return horaMedicaRepository.save(hora);
        }
        return null;
    }

    public HoraMedica cancelarHoraMedica(Long id) {
        Optional<HoraMedica> horaExistente = horaMedicaRepository.findById(id);

        if (horaExistente.isPresent()) {
            HoraMedica hora = horaExistente.get();
            hora.setEstado(EstadoHora.CANCELADA);
            return horaMedicaRepository.save(hora);
        }
        return null;
    }
    
}