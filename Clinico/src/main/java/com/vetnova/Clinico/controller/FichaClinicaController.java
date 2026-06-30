package com.vetnova.Clinico.controller;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.vetnova.Clinico.model.FichaClinica;
import com.vetnova.Clinico.model.FichaClinicaDTO;
import com.vetnova.Clinico.service.FichaClinicaService;

@RestController
@RequestMapping("/api/ficha")
public class FichaClinicaController {
    @Autowired
    private FichaClinicaService fichaClinicaService;

    @Operation(summary = "Crea la ficha clínica de una mascota")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ficha clínica creada"),
        @ApiResponse(responseCode = "409", description = "Conflicto al crear la ficha")
    })
    @PostMapping
    public ResponseEntity<FichaClinica> postFichaClinica(@RequestBody FichaClinicaDTO dto) {
        try {
            return new ResponseEntity<>(fichaClinicaService.guardarFichaClinica(dto),HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }        
    }

    @Operation(summary = "Lista todas las fichas clínicas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de fichas obtenido")
    })
    @GetMapping()
    public List<FichaClinica> getFichasClinicas() {
        return fichaClinicaService.listarFichasClinicas();
    }

    @Operation(summary = "Obtiene una ficha clínica por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ficha encontrada"),
        @ApiResponse(responseCode = "204", description = "La ficha no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FichaClinica> getFichaClinicaxId(@PathVariable Long id) {
        FichaClinica buscado=fichaClinicaService.findById(id).orElse(null);
        if(buscado==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscado,HttpStatus.OK);
    }

    @Operation(summary = "Busca fichas clínicas por nombre de mascota")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fichas encontradas"),
        @ApiResponse(responseCode = "204", description = "No hay fichas con ese nombre de mascota")
    })
    @GetMapping("/buscar/nombreMascota/{nombreMascota}")
    public ResponseEntity<List<FichaClinica>> getFichaClinicaxNombreMascota(@PathVariable String nombreMascota) {
        List<FichaClinica> buscados = fichaClinicaService.findByNombreMascota(nombreMascota);
        if(buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados,HttpStatus.OK);
    }

    @Operation(summary = "Busca fichas clínicas por nombre del dueño")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fichas encontradas"),
        @ApiResponse(responseCode = "204", description = "No hay fichas para ese dueño")
    })
    @GetMapping("/buscar/nombreDueño/{nombreDueño}")
    public ResponseEntity<List<FichaClinica>> getFichaClinicaxNombreDueño(@PathVariable String nombreDueño) {
        List<FichaClinica> buscados = fichaClinicaService.findByNombreDueño(nombreDueño);
        if(buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados,HttpStatus.OK);
    }

    @Operation(summary = "Busca fichas clínicas por rut del dueño")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Fichas encontradas"),
        @ApiResponse(responseCode = "204", description = "No hay fichas para ese rut")
    })
    @GetMapping("/buscar/rutDueño/{rutDueño}")
    public ResponseEntity<List<FichaClinica>> getFichaClinicaxRutDueño(@PathVariable String rutDueño) {
        List<FichaClinica> buscados = fichaClinicaService.findByRutDueño(rutDueño);
        if(buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados,HttpStatus.OK);
    }

    @Operation(summary = "Actualiza parcialmente una ficha clínica (alergias y peso)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ficha actualizada"),
        @ApiResponse(responseCode = "404", description = "La ficha no existe")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<FichaClinica> patchFichaClinica(@PathVariable Long id, @RequestBody FichaClinica datosaCambiar) {
        FichaClinica actualizado = fichaClinicaService.actualizarFichaClinica(id, datosaCambiar);
        
        if (actualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @Operation(summary = "Elimina una ficha clínica por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Ficha eliminada"),
        @ApiResponse(responseCode = "404", description = "La ficha no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFichaClinica(@PathVariable Long id) {
        boolean eliminado = fichaClinicaService.eliminarFichaClinica(id);
        if (!eliminado) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
}