package vetnova.ventas.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vetnova.ventas.client.dto.UsuarioDTO;
import vetnova.ventas.client.dto.ValidacionTokenDTO;
import vetnova.ventas.exception.ResourceNotFoundException;
import vetnova.ventas.exception.ServicioExternoException;

/**
 * Cliente REST hacia el microservicio de Autenticacion (puerto 8081).
 * Ventas lo usa para validar tokens de clientes y para obtener el RUT al emitir boletas/facturas.
 */
@Component
@Slf4j
public class AuthClient {

    private final RestTemplate restTemplate;
    private final String authBaseUrl;

    public AuthClient(RestTemplate restTemplate, @Value("${vetnova.auth.url}") String authBaseUrl) {
        this.restTemplate = restTemplate;
        this.authBaseUrl = authBaseUrl;
    }

    public boolean tokenEsValido(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            String url = UriComponentsBuilder.fromUriString(authBaseUrl + "/api/auth/validar")
                    .queryParam("token", token)
                    .toUriString();
            ValidacionTokenDTO respuesta = restTemplate.getForObject(url, ValidacionTokenDTO.class);
            return respuesta != null && respuesta.isValido();
        } catch (RestClientException ex) {
            log.warn("No fue posible validar el token contra Autenticación: {}", ex.getMessage());
            return false;
        }
    }

    public UsuarioDTO obtenerUsuarioPorId(Long usuarioId) {
        try {
            UsuarioDTO usuario = restTemplate.getForObject(
                    authBaseUrl + "/api/usuarios/{id}", UsuarioDTO.class, usuarioId);
            if (usuario == null) {
                throw new ResourceNotFoundException("No se encontró el usuario con id: " + usuarioId);
            }
            return usuario;
        } catch (RestClientException ex) {
            log.error("Error al consultar el usuario {} en Autenticación: {}", usuarioId, ex.getMessage());
            throw new ServicioExternoException(
                    "No fue posible comunicarse con el microservicio de Autenticación para obtener el cliente");
        }
    }
}
