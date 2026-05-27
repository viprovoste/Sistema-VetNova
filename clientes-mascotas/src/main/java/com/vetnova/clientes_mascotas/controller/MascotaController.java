package com.vetnova.clientes_mascotas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.vetnova.clientes_mascotas.model.Mascota;
import com.vetnova.clientes_mascotas.model.MascotaDTO;
import com.vetnova.clientes_mascotas.service.MascotaService;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {
    @Autowired
    private MascotaService mascotaService;

    @GetMapping
    public ResponseEntity<List<Mascota>> getMascotas() {
        List<Mascota> mascotas = mascotaService.listarMascotas();
        if (mascotas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(mascotas, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Mascota> postMascota(@Valid @RequestBody MascotaDTO dto) {
        try {
            Mascota nuevo = mascotaService.guardarMascota(dto);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mascota> getMascota(@PathVariable Long id) {
        Mascota buscado=mascotaService.findById(id).orElse(null);
        if(buscado==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscado,HttpStatus.OK);
    }
    
    @GetMapping("/buscar/nombre/{nombre}")
    public ResponseEntity<List<Mascota>> getMascotaxNombre(@PathVariable String nombre) {
        List<Mascota> buscados = mascotaService.findByNombre(nombre);
        if(buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados,HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Mascota> patchMascota(@PathVariable Long id, @RequestBody Mascota datosaCambiar) {
        Mascota actualizado = mascotaService.actualizarMascota(id, datosaCambiar);
        
        if (actualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @PatchMapping("/desactivar/{id}")
    public ResponseEntity<Mascota> desactivarMascota(@PathVariable Long id) {
        Mascota desactivada = mascotaService.desactivarMascota(id);
        if (desactivada == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(desactivada, HttpStatus.OK);
    }
    
}
