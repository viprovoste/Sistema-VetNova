package vetnova.ventas.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import vetnova.ventas.client.dto.MovimientoRequestDTO;
import vetnova.ventas.client.dto.ProductoDTO;
import vetnova.ventas.client.dto.VerificarStockRequestDTO;
import vetnova.ventas.client.dto.VerificarStockResponseDTO;
import vetnova.ventas.exception.ResourceNotFoundException;
import vetnova.ventas.exception.ServicioExternoException;

/**
 * Cliente REST hacia el microservicio de Inventario y Bodega (puerto 8085).
 * Ventas lo usa para: (1) obtener nombre/precio vigente de un producto al agregarlo al
 * carrito, (2) verificar disponibilidad antes de aceptar una compra, y (3) registrar la
 * salida de stock cuando una venta se paga.
 */
@Component
@Slf4j
public class InventarioClient {

    private final RestTemplate restTemplate;
    private final String inventarioBaseUrl;

    public InventarioClient(RestTemplate restTemplate, @Value("${vetnova.inventario.url}") String inventarioBaseUrl) {
        this.restTemplate = restTemplate;
        this.inventarioBaseUrl = inventarioBaseUrl;
    }

    public ProductoDTO obtenerProducto(Long productoId) {
        try {
            ProductoDTO producto = restTemplate.getForObject(
                    inventarioBaseUrl + "/api/productos/{id}", ProductoDTO.class, productoId);
            if (producto == null) {
                throw new ResourceNotFoundException("No se encontró el producto con id: " + productoId);
            }
            return producto;
        } catch (RestClientException ex) {
            log.error("Error al consultar el producto {} en Inventario: {}", productoId, ex.getMessage());
            throw new ServicioExternoException(
                    "No fue posible comunicarse con el microservicio de Inventario para consultar el producto");
        }
    }

    public VerificarStockResponseDTO verificarDisponibilidad(VerificarStockRequestDTO request) {
        try {
            VerificarStockResponseDTO respuesta = restTemplate.postForObject(
                    inventarioBaseUrl + "/api/stock/verificar-disponibilidad", request, VerificarStockResponseDTO.class);
            if (respuesta == null) {
                throw new ServicioExternoException("Inventario no devolvió una respuesta válida al verificar stock");
            }
            return respuesta;
        } catch (RestClientException ex) {
            log.error("Error al verificar disponibilidad de stock en Inventario: {}", ex.getMessage());
            throw new ServicioExternoException(
                    "No fue posible comunicarse con el microservicio de Inventario para verificar stock");
        }
    }

    public void registrarSalida(MovimientoRequestDTO movimiento) {
        try {
            restTemplate.postForObject(inventarioBaseUrl + "/api/movimientos", movimiento, Object.class);
        } catch (RestClientException ex) {
            log.error("Error al registrar la salida de stock en Inventario: {}", ex.getMessage());
            throw new ServicioExternoException(
                    "No fue posible registrar la salida de stock en el microservicio de Inventario");
        }
    }
}
