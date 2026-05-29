package com.vetnova.clientes_mascotas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetnova.clientes_mascotas.model.Cliente;
import com.vetnova.clientes_mascotas.model.Mascota;
import com.vetnova.clientes_mascotas.model.MascotaDTO;
import com.vetnova.clientes_mascotas.repository.ClienteRepository;
import com.vetnova.clientes_mascotas.repository.MascotaRepository;

@Service
public class MascotaService {
    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public Mascota guardarMascota(MascotaDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Mascota mascota = new Mascota();
        mascota.setNombre(dto.getNombre());
        mascota.setEspecie(dto.getEspecie());
        mascota.setPeso(dto.getPeso());
        mascota.setCliente(cliente);
        return mascotaRepository.save(mascota);
    }

    public List<Mascota> listarMascotas() {
        return mascotaRepository.findAll();
    }

    public Optional<Mascota> findById(Long id) {
        return mascotaRepository.findById(id);
    }

    public List<Mascota> findByNombre(String nombre) {
        return mascotaRepository.findByNombre(nombre);
    }

    public Mascota actualizarMascota(Long id, Mascota datosaCambiar) {
        Optional<Mascota> mascotaExistente = mascotaRepository.findById(id);
        if (mascotaExistente.isPresent()) {
            Mascota mascota = mascotaExistente.get();
            if (datosaCambiar.getNombre() != null) mascota.setNombre(datosaCambiar.getNombre());
            if (datosaCambiar.getEspecie() != null) mascota.setEspecie(datosaCambiar.getEspecie());
            if (datosaCambiar.getPeso() != null) mascota.setPeso(datosaCambiar.getPeso());
            return mascotaRepository.save(mascota);
        }
        return null;
    }

    public boolean eliminarMascota(Long id) {
        if (mascotaRepository.existsById(id)) {
            mascotaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}