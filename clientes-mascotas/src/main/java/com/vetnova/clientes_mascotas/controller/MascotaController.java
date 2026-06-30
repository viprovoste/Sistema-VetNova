package com.vetnova.clientes_mascotas.controller;

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
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.vetnova.clientes_mascotas.model.Mascota;
import com.vetnova.clientes_mascotas.model.MascotaDTO;
import com.vetnova.clientes_mascotas.service.MascotaService;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {
    @Autowired
    private MascotaService mascotaService;

    @Operation(summary = "Lista todas las mascotas registradas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de mascotas obtenido"),
        @ApiResponse(responseCode = "204", description = "No hay mascotas registradas")
    })
    @GetMapping
    public ResponseEntity<List<Mascota>> getMascotas() {
        List<Mascota> mascotas = mascotaService.listarMascotas();
        if (mascotas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(mascotas, HttpStatus.OK);
    }

    @Operation(summary = "Registra una nueva mascota asociada a un cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Mascota creada correctamente"),
        @ApiResponse(responseCode = "400", description = "El cliente dueño no existe")
    })
    @PostMapping
    public ResponseEntity<Mascota> postMascota(@Valid @RequestBody MascotaDTO dto) {
        try {
            Mascota nuevo = mascotaService.guardarMascota(dto);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Obtiene una mascota por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mascota encontrada"),
        @ApiResponse(responseCode = "204", description = "La mascota no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Mascota> getMascotaxId(@PathVariable Long id) {
        Mascota buscado=mascotaService.findById(id).orElse(null);
        if(buscado==null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscado,HttpStatus.OK);
    }

    @Operation(summary = "Busca mascotas por su nombre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mascotas encontradas"),
        @ApiResponse(responseCode = "204", description = "No hay mascotas con ese nombre")
    })
    @GetMapping("/buscar/nombre/{nombre}")
    public ResponseEntity<List<Mascota>> getMascotaxNombre(@PathVariable String nombre) {
        List<Mascota> buscados = mascotaService.findByNombre(nombre);
        if(buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados,HttpStatus.OK);
    }

    @Operation(summary = "Actualiza parcialmente los datos de una mascota")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mascota actualizada"),
        @ApiResponse(responseCode = "404", description = "La mascota no existe")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Mascota> patchMascota(@PathVariable Long id, @RequestBody Mascota datosaCambiar) {
        Mascota actualizado = mascotaService.actualizarMascota(id, datosaCambiar);
        
        if (actualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @Operation(summary = "Elimina una mascota por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Mascota eliminada"),
        @ApiResponse(responseCode = "404", description = "La mascota no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMascota(@PathVariable Long id) {
        boolean eliminado = mascotaService.eliminarMascota(id);
        if (!eliminado) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
}