package com.soporte.soporte.client;

import com.soporte.soporte.dto.SoporteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "reporte-service", url = "http://localhost:8089")
public interface ReporteClient {

    @PostMapping("/api/reportes")
    void registrarDato(@RequestBody SoporteDTO soporteDTO);
}