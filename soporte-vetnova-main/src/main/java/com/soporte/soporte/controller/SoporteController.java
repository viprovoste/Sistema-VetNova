package com.soporte.soporte.controller;

import com.soporte.soporte.model.Soporte;
import com.soporte.soporte.service.SoporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/soportes")
public class SoporteController {

    @Autowired
    private SoporteService soporteService;

    @PostMapping
    public Soporte crearSoporte(@RequestBody Soporte soporte) {
        return soporteService.guardar(soporte);
    }
}