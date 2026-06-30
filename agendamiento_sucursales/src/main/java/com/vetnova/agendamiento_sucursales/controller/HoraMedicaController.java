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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.vetnova.agendamiento_sucursales.model.HoraMedica;
import com.vetnova.agendamiento_sucursales.model.HoraMedicaDTO;
import com.vetnova.agendamiento_sucursales.service.HoraMedicaService;

@RestController
@RequestMapping("/api/horas")
public class HoraMedicaController {
    @Autowired
    private HoraMedicaService horaMedicaService;

    @Operation(summary = "Agenda una nueva hora médica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Hora médica agendada en estado CONFIRMADA"),
        @ApiResponse(responseCode = "400", description = "Mascota, cliente o box no válidos"),
        @ApiResponse(responseCode = "409", description = "Conflicto durante el agendamiento")
    })
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

    @Operation(summary = "Busca horas médicas por nombre de mascota")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horas encontradas"),
        @ApiResponse(responseCode = "204", description = "No hay horas para esa mascota")
    })
    @GetMapping("/buscar/mascota/{nombre}")
    public ResponseEntity<List<HoraMedica>> getHorasxNombreMascota(@PathVariable String nombre) {
        List<HoraMedica> buscados = horaMedicaService.listarPorNombreMascota(nombre);
        if (buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados, HttpStatus.OK);
    }

    @Operation(summary = "Busca horas médicas por nombre del dueño")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horas encontradas"),
        @ApiResponse(responseCode = "204", description = "No hay horas para ese dueño")
    })
    @GetMapping("/buscar/dueño/{nombre}")
    public ResponseEntity<List<HoraMedica>> getHorasxNombreDueño(@PathVariable String nombre) {
        List<HoraMedica> buscados = horaMedicaService.listarPorNombreDueño(nombre);
        if (buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados, HttpStatus.OK);
    }

    @Operation(summary = "Busca horas médicas por rut del dueño")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horas encontradas"),
        @ApiResponse(responseCode = "204", description = "No hay horas para ese rut")
    })
    @GetMapping("/buscar/rut/{rut}")
    public ResponseEntity<List<HoraMedica>> getHorasxRut(@PathVariable String rut) {
        List<HoraMedica> buscados = horaMedicaService.listarPorRutDueño(rut);
        if (buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados, HttpStatus.OK);
    }

    @Operation(summary = "Busca horas médicas por nombre del veterinario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horas encontradas"),
        @ApiResponse(responseCode = "204", description = "No hay horas para ese veterinario")
    })
    @GetMapping("/buscar/veterinario/{nombre}")
    public ResponseEntity<List<HoraMedica>> getHorasxVeterinario(@PathVariable String nombre) {
        List<HoraMedica> buscados = horaMedicaService.listarPorVeterinario(nombre);
        if (buscados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(buscados, HttpStatus.OK);
    }

    @Operation(summary = "Reprograma parcialmente una hora médica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hora médica actualizada"),
        @ApiResponse(responseCode = "404", description = "La hora médica no existe")
    })
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

    @Operation(summary = "Cancela una hora médica (cancelación lógica, estado CANCELADA)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hora médica cancelada"),
        @ApiResponse(responseCode = "404", description = "La hora médica no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HoraMedica> cancelarHoraMedica(@PathVariable Long id) {
        HoraMedica cancelado = horaMedicaService.cancelarHoraMedica(id);
        if (cancelado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cancelado, HttpStatus.OK);
    }
}