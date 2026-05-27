package com.soporte.soporte.client;

import com.soporte.soporte.dto.SoporteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notificacion-service", url = "http://localhost:8089")
public interface NotificacionClient {

    @PostMapping("/api/notificaciones")
    void enviarNotificacion(@RequestBody SoporteDTO soporteDTO);

}