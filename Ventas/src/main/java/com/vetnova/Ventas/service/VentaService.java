package com.vetnova.Ventas.service;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.vetnova.Ventas.config.RabbitMQConfig;
import com.vetnova.Ventas.dto.EventoVentaDto;
import com.vetnova.Ventas.dto.VentaRequest;
import com.vetnova.Ventas.model.Venta;
import com.vetnova.Ventas.repository.VentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaService {
    private final VentaRepository ventaRepository;
    private final RabbitTemplate rabbitTemplate; // El disparador de mensajes de Spring

    public String ejecutarVenta(VentaRequest request) {
        // 1. Registrar la venta en la Base de Datos local de Ventas
        Venta nuevaVenta = Venta.builder()
                .productoId(request.productoId())
                .cantidad(request.cantidad())
                .total(request.cantidad() * request.precioUnitario())
                .fechaVenta(LocalDateTime.now())
                .build();

        ventaRepository.save(nuevaVenta);
        System.out.println("💾 Venta guardada en la base de datos de Ventas.");

        // 2. Crear el evento que necesita el Inventario
        EventoVentaDto evento = new EventoVentaDto(request.productoId(), request.cantidad());

        // 3. ENVIAR ASINCRÓNICAMENTE A RABBITMQ
        // Ventas no sabe NI LE IMPORTA si inventario está caído, lento o ocupado. Ventas cumple con avisar.
        rabbitTemplate.convertAndSend(RabbitMQConfig.COLA_INVENTARIO, evento);
        System.out.println("🚀 Evento de venta despachado hacia el Broker (RabbitMQ).");

        return "Venta procesada exitosamente. ID Venta: " + nuevaVenta.getId();
    }
}
