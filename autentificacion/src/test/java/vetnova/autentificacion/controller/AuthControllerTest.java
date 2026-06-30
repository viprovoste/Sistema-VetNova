package vetnova.autentificacion.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import vetnova.autentificacion.dto.LoginRequest;
import vetnova.autentificacion.dto.LoginResponse;
import vetnova.autentificacion.dto.UsuarioResponse;
import vetnova.autentificacion.dto.ValidacionTokenResponse;
import vetnova.autentificacion.model.Rol;
import vetnova.autentificacion.service.AuthService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - pruebas unitarias")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(authService);
    }

    @Test
    @DisplayName("login() delega en el servicio y responde con el token")
    void loginDelega() {
        LoginRequest request = new LoginRequest("carla@vetnova.cl", "clave123");
        LoginResponse esperado = LoginResponse.builder().token("token-abc").usuarioId(1L)
                .nombre("Carla").email("carla@vetnova.cl").rol(Rol.RECEPCIONISTA).build();
        when(authService.login(request)).thenReturn(esperado);

        ResponseEntity<LoginResponse> response = controller.login(request);

        assertThat(response.getBody().getToken()).isEqualTo("token-abc");
    }

    @Test
    @DisplayName("logout() invalida el token y responde 204 NO_CONTENT")
    void logoutDelega() {
        ResponseEntity<Void> response = controller.logout("token-abc");

        verify(authService).logout("token-abc");
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    @DisplayName("validar() delega en el servicio")
    void validarDelega() {
        when(authService.validarToken("token-abc"))
                .thenReturn(ValidacionTokenResponse.builder().valido(true).usuarioId(1L).build());

        ResponseEntity<ValidacionTokenResponse> response = controller.validar("token-abc");

        assertThat(response.getBody().isValido()).isTrue();
    }

    @Test
    @DisplayName("usuarioPorToken() delega en el servicio")
    void usuarioPorTokenDelega() {
        UsuarioResponse esperado = UsuarioResponse.builder().id(1L).nombre("Carla").build();
        when(authService.obtenerUsuarioPorToken("token-abc")).thenReturn(esperado);

        ResponseEntity<UsuarioResponse> response = controller.usuarioPorToken("token-abc");

        assertThat(response.getBody().getId()).isEqualTo(1L);
    }
}
