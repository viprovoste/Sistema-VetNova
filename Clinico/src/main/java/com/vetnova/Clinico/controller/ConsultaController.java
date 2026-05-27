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

import com.vetnova.Clinico.model.Consulta;
import com.vetnova.Clinico.service.ConsultaService;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {
    @Autowired
    private ConsultaService consultaService;

    @PostMapping("/{idFicha}")
    public ResponseEntity<Consulta> postConsulta(@PathVariable Long idFicha, @RequestBody Consulta consulta) {
        try {
        Consulta nuevo = consultaService.guardarConsulta(idFicha, consulta);
        if (nuevo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
        }
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/ficha/{idFicha}")
    public ResponseEntity<List<Consulta>> getConsultasPorFicha(@PathVariable Long idFicha) {
        List<Consulta> consultas = consultaService.listarConsultasPorFicha(idFicha);
        if (consultas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(consultas, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Consulta> patchConsulta(@PathVariable Long id, @RequestBody Consulta datosaCambiar) {
        Consulta actualizado = consultaService.actualizarConsulta(id, datosaCambiar);
        
        if (actualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }
    
}
