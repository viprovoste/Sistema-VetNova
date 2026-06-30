package vetnova.inventario.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import vetnova.inventario.dto.ValidacionTokenDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthClient (Inventario) - pruebas unitarias")
class AuthClientTest {

    @Mock
    private RestTemplate restTemplate;

    private AuthClient authClient;

    @BeforeEach
    void setUp() {
        authClient = new AuthClient(restTemplate, "http://localhost:8081");
    }

    @Test
    @DisplayName("Devuelve true cuando Autenticación confirma que el token es válido")
    void devuelveTrueCuandoTokenValido() {
        ValidacionTokenDTO respuesta = new ValidacionTokenDTO(true, 1L);
        when(restTemplate.getForObject(anyString(), any())).thenReturn(respuesta);

        boolean resultado = authClient.tokenEsValido("token-abc");

        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("Devuelve false cuando Autenticación responde que el token no es válido")
    void devuelveFalseCuandoTokenInvalido() {
        ValidacionTokenDTO respuesta = new ValidacionTokenDTO(false, null);
        when(restTemplate.getForObject(anyString(), any())).thenReturn(respuesta);

        boolean resultado = authClient.tokenEsValido("token-vencido");

        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("Devuelve false (fail-safe) si Autenticación no responde")
    void devuelveFalseSiServicioNoResponde() {
        when(restTemplate.getForObject(anyString(), any())).thenThrow(new ResourceAccessException("timeout"));

        boolean resultado = authClient.tokenEsValido("token-cualquiera");

        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("Devuelve false si el token es nulo o vacío, sin llamar al servicio")
    void devuelveFalseSiTokenVacio() {
        assertThat(authClient.tokenEsValido(null)).isFalse();
        assertThat(authClient.tokenEsValido("")).isFalse();
        assertThat(authClient.tokenEsValido("   ")).isFalse();
    }
}
