package com.vetnova.Clinico.controller;

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

import com.vetnova.Clinico.model.FichaClinica;
import com.vetnova.Clinico.model.FichaClinicaDTO;
import com.vetnova.Clinico.service.FichaClinicaService;

@RestController
@RequestMapping("/api/ficha")
public class FichaClinicaController {
    @Autowired
    private FichaClinicaService fichaClinicaService;

    @PostMapping
    public ResponseEntity<FichaClinica> postFichaClinica(@RequestBody FichaClinicaDTO dto) {
        try {
            return new ResponseEntity<>(fichaClinicaService.guardarFichaClinica(dto),HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }        
    }

    @GetMapping()
    public List<FichaClinica> getFichasClinicas() {
        return fichaClinicaService.listarFichasClinicas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FichaClinica> getFichaClinicaxId(@PathVariable Long id) {
        FichaClinica buscado=fichaClinicaService.findById(id).orElse(null);
        if(buscado==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscado,HttpStatus.OK);
    }

    @GetMapping("/buscar/nombreMascota/{nombreMascota}")
    public ResponseEntity<List<FichaClinica>> getFichaClinicaxNombreMascota(@PathVariable String nombreMascota) {
        List<FichaClinica> buscados = fichaClinicaService.findByNombreMascota(nombreMascota);
        if(buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados,HttpStatus.OK);
    }

    @GetMapping("/buscar/nombreDueño/{nombreDueño}")
    public ResponseEntity<List<FichaClinica>> getFichaClinicaxNombreDueño(@PathVariable String nombreDueño) {
        List<FichaClinica> buscados = fichaClinicaService.findByNombreDueño(nombreDueño);
        if(buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados,HttpStatus.OK);
    }

    @GetMapping("/buscar/rutDueño/{rutDueño}")
    public ResponseEntity<List<FichaClinica>> getFichaClinicaxRutDueño(@PathVariable String rutDueño) {
        List<FichaClinica> buscados = fichaClinicaService.findByRutDueño(rutDueño);
        if(buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados,HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FichaClinica> patchFichaClinica(@PathVariable Long id, @RequestBody FichaClinica datosaCambiar) {
        FichaClinica actualizado = fichaClinicaService.actualizarFichaClinica(id, datosaCambiar);
        
        if (actualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @PatchMapping("/desactivar/{id}")
    public ResponseEntity<FichaClinica> desactivarFichaClinica(@PathVariable Long id) {
        FichaClinica desactivada = fichaClinicaService.desactivarFichaClinica(id);
        if (desactivada == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(desactivada, HttpStatus.OK);
    }
    
}