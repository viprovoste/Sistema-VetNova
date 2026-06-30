package vetnova.ventas.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import vetnova.ventas.client.dto.UsuarioDTO;
import vetnova.ventas.client.dto.ValidacionTokenDTO;
import vetnova.ventas.exception.ResourceNotFoundException;
import vetnova.ventas.exception.ServicioExternoException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthClient (Ventas) - pruebas unitarias")
class AuthClientTest {

    @Mock
    private RestTemplate restTemplate;

    private AuthClient authClient;

    @BeforeEach
    void setUp() {
        authClient = new AuthClient(restTemplate, "http://localhost:8081");
    }

    @Nested
    @DisplayName("tokenEsValido")
    class TokenEsValido {

        @Test
        @DisplayName("Devuelve true cuando Autenticación confirma el token")
        void devuelveTrue() {
            when(restTemplate.getForObject(anyString(), eq(ValidacionTokenDTO.class)))
                    .thenReturn(new ValidacionTokenDTO(true, 1L));

            assertThat(authClient.tokenEsValido("token-abc")).isTrue();
        }

        @Test
        @DisplayName("Devuelve false (fail-safe) si Autenticación no responde")
        void devuelveFalseSiFalla() {
            when(restTemplate.getForObject(anyString(), eq(ValidacionTokenDTO.class)))
                    .thenThrow(new ResourceAccessException("timeout"));

            assertThat(authClient.tokenEsValido("token-abc")).isFalse();
        }

        @Test
        @DisplayName("Devuelve false sin llamar al servicio si el token viene vacío")
        void devuelveFalseSiTokenVacio() {
            assertThat(authClient.tokenEsValido(" ")).isFalse();
            assertThat(authClient.tokenEsValido(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("obtenerUsuarioPorId")
    class ObtenerUsuarioPorId {

        @Test
        @DisplayName("Devuelve el usuario cuando Autenticación responde correctamente")
        void devuelveUsuario() {
            UsuarioDTO usuario = new UsuarioDTO(1L, "Carla", "Soto", "carla@vetnova.cl", "11111111-1");
            when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class), eq(1L)))
                    .thenReturn(usuario);

            UsuarioDTO resultado = authClient.obtenerUsuarioPorId(1L);

            assertThat(resultado.getRut()).isEqualTo("11111111-1");
        }

        @Test
        @DisplayName("Lanza ResourceNotFoundException si Autenticación devuelve null")
        void lanzaNotFoundSiRespuestaNula() {
            when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class), eq(404L)))
                    .thenReturn(null);

            assertThatThrownBy(() -> authClient.obtenerUsuarioPorId(404L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Lanza ServicioExternoException si Autenticación no responde")
        void lanzaServicioExternoSiFalla() {
            when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class), eq(1L)))
                    .thenThrow(new ResourceAccessException("timeout"));

            assertThatThrownBy(() -> authClient.obtenerUsuarioPorId(1L))
                    .isInstanceOf(ServicioExternoException.class);
        }
    }
}
