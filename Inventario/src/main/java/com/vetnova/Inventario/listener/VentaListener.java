package com.vetnova.Inventario.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.vetnova.Inventario.config.RabbitMQConfig;
import com.vetnova.Inventario.dto.EventoVentaDto;
import com.vetnova.Inventario.model.Producto;
import com.vetnova.Inventario.repository.ProductoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VentaListener {
    private final ProductoRepository productoRepository;

    @RabbitListener(queues = RabbitMQConfig.COLA_INVENTARIO)
    @Transactional
    public void procesarDescuentoInventario(EventoVentaDto evento) {
        System.out.println("📬 Evento recibido de Ventas. Procesando Producto ID: " + evento.productoId());

        // 1. Buscar el producto en la BD de inventario
        Producto producto = productoRepository.findById(evento.productoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en inventario: " + evento.productoId()));

        // 2. Validar que tengamos stock suficiente
        if (producto.getStock() < evento.cantidad()) {
            System.err.println("❌ ERROR: Stock insuficiente para el producto: " + producto.getNombre());
            // En producción, aquí lanzarías un evento de "VentaRechazada" de vuelta a Ventas
            return;
        }

        // 3. Descontar y guardar
        producto.setStock(producto.getStock() - evento.cantidad());
        productoRepository.save(producto);

        System.out.println("✅ Stock actualizado con éxito. Nuevo stock de " + producto.getNombre() + ": " + producto.getStock());
    }
}
