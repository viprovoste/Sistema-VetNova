package com.vetnova.agendamiento_sucursales.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetnova.agendamiento_sucursales.model.HoraMedica;
import com.vetnova.agendamiento_sucursales.model.HoraMedicaDTO;
import com.vetnova.agendamiento_sucursales.service.HoraMedicaService;

@RestController
@RequestMapping("/api/horas")
public class HoraMedicaController {
    @Autowired
    private HoraMedicaService horaMedicaService;

    @PostMapping 
    public ResponseEntity<HoraMedica> postHoraMedica(@RequestBody HoraMedicaDTO dto) {
        try {
            HoraMedica nuevo = horaMedicaService.guardarHoraMedica(dto);
            if (nuevo == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/buscar/mascota/{nombre}")
    public ResponseEntity<List<HoraMedica>> getHorasxNombreMascota(@PathVariable String nombre) {
        List<HoraMedica> buscados = horaMedicaService.listarPorNombreMascota(nombre);
        if (buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados, HttpStatus.OK);
    }
    
    @GetMapping("/buscar/dueño/{nombre}")
    public ResponseEntity<List<HoraMedica>> getHorasxNombreDueño(@PathVariable String nombre) {
        List<HoraMedica> buscados = horaMedicaService.listarPorNombreDueño(nombre);
        if (buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados, HttpStatus.OK);
    }

    @GetMapping("/buscar/rut/{rut}")
    public ResponseEntity<List<HoraMedica>> getHorasxRut(@PathVariable String rut) {
        List<HoraMedica> buscados = horaMedicaService.listarPorRutDueño(rut);
        if (buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados, HttpStatus.OK);
    }

    @GetMapping("/buscar/veterinario/{nombre}")
    public ResponseEntity<List<HoraMedica>> getHorasxVeterinario(@PathVariable String nombre) {
        List<HoraMedica> buscados = horaMedicaService.listarPorVeterinario(nombre);
        if (buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HoraMedica> patchHoraMedica(
            @PathVariable Long id,
            @RequestBody HoraMedicaDTO dto) {
        HoraMedica actualizado = horaMedicaService.actualizarHoraMedica(id, dto);
        if (actualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HoraMedica> cancelarHoraMedica(@PathVariable Long id) {
        HoraMedica cancelado = horaMedicaService.cancelarHoraMedica(id);
        if (cancelado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cancelado, HttpStatus.OK);
    }
}