package com.vetnova.Clinico.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vetnova.Clinico.model.ClienteDTO;
import com.vetnova.Clinico.model.FichaClinica;
import com.vetnova.Clinico.model.FichaClinicaDTO;
import com.vetnova.Clinico.model.MascotaDTO;
import com.vetnova.Clinico.repository.FichaClinicaRepository;

@Service
public class FichaClinicaService {
    @Autowired
    private FichaClinicaRepository fichaClinicaRepository;

    @Autowired
    private RestTemplate restTemplate;

     public FichaClinica guardarFichaClinica(FichaClinicaDTO dto) {
        String urlMascota = "http://localhost:8082/api/mascotas/" + dto.getIdMascota();
        MascotaDTO mascota = restTemplate.getForObject(urlMascota, MascotaDTO.class);

        String urlCliente = "http://localhost:8082/api/clientes/" + dto.getIdCliente();
        ClienteDTO cliente = restTemplate.getForObject(urlCliente, ClienteDTO.class);

        if (mascota != null && cliente != null) {
            FichaClinica ficha = new FichaClinica();
 
            ficha.setIdMascota(dto.getIdMascota());
            ficha.setIdCliente(dto.getIdCliente());
 
            ficha.setNombreMascota(mascota.getNombre());
            ficha.setEspecie(mascota.getEspecie());
            ficha.setPeso(mascota.getPeso());
 
            ficha.setNombreDueño(cliente.getNombres());
            ficha.setApellidoDueño(cliente.getApellidos());
            ficha.setRutDueño(cliente.getRut());
 
            return fichaClinicaRepository.save(ficha);
        }         
        return null;
    }

    public List<FichaClinica> listarFichasClinicas() {
        return fichaClinicaRepository.findAll();
    }
    
    public Optional<FichaClinica> findById(Long id) {
        return fichaClinicaRepository.findById(id);
    }

    public List<FichaClinica> findByNombreMascota(String nombreMascota) {
        return fichaClinicaRepository.findByNombreMascota(nombreMascota);
    }

    public List<FichaClinica> findByNombreDueño(String nombreDueño) {
        return fichaClinicaRepository.findByNombreDueño(nombreDueño);
    }

    public List<FichaClinica> findByRutDueño(String rutDueño) {
        return fichaClinicaRepository.findByRutDueño(rutDueño);
    }

    public FichaClinica actualizarFichaClinica(Long id, FichaClinica datosaCambiar) {
        Optional<FichaClinica> fichaExistente = fichaClinicaRepository.findById(id);
 
        if (fichaExistente.isPresent()) {
            FichaClinica ficha = fichaExistente.get();
 
            if (datosaCambiar.getAlergias() != null) {
                ficha.setAlergias(datosaCambiar.getAlergias());
            }
            if (datosaCambiar.getPeso() != null) {
                ficha.setPeso(datosaCambiar.getPeso());
            }
 
            return fichaClinicaRepository.save(ficha);
        }
        return null;
    }
}
