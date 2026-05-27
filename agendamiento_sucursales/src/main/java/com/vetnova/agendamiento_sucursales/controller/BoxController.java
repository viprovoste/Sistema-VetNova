package com.vetnova.agendamiento_sucursales.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vetnova.agendamiento_sucursales.model.Box;
import com.vetnova.agendamiento_sucursales.model.EstadoBox;
import com.vetnova.agendamiento_sucursales.service.BoxService;

@RestController
@RequestMapping("/api/boxes")
public class BoxController {
    @Autowired
    private BoxService boxService;

    @GetMapping("/sucursal/{idSucursal}")
    public ResponseEntity<List<Box>> getBoxesPorSucursal(@PathVariable Long idSucursal) {
        List<Box> boxes = boxService.listarBoxesPorSucursal(idSucursal);
        if (boxes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(boxes, HttpStatus.OK);
    }
    
    @GetMapping("/sucursal/{idSucursal}/estado")
    public ResponseEntity<List<Box>> getBoxesPorEstado(
            @PathVariable Long idSucursal,
            @RequestParam EstadoBox estado) {
        List<Box> boxes = boxService.listarBoxesPorEstado(idSucursal, estado);
        if (boxes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(boxes, HttpStatus.OK);
    }

    @PatchMapping("/{idBox}")
    public ResponseEntity<Box> patchEstadoBox(
            @PathVariable Long idBox,
            @RequestParam EstadoBox estado) {
        Box actualizado = boxService.actualizarEstadoBox(idBox, estado);
        if (actualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }
}
