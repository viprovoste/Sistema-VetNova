package vetnova.autentificacion.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import vetnova.autentificacion.dto.LoginRequest;
import vetnova.autentificacion.dto.LoginResponse;
import vetnova.autentificacion.dto.UsuarioResponse;
import vetnova.autentificacion.dto.ValidacionTokenResponse;
import vetnova.autentificacion.exception.CredencialesInvalidasException;
import vetnova.autentificacion.exception.ResourceNotFoundException;
import vetnova.autentificacion.model.Credencial;
import vetnova.autentificacion.model.Rol;
import vetnova.autentificacion.model.Usuario;
import vetnova.autentificacion.repository.CredencialRepository;
import vetnova.autentificacion.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - pruebas unitarias con 100% de cobertura")
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private CredencialRepository credencialRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioActivo;

    @BeforeEach
    void setUp() {
        usuarioActivo = Usuario.builder()
                .id(1L).nombre("Carla").email("carla@vetnova.cl")
                .passwordHash("hash-guardado").rol(Rol.RECEPCIONISTA).activo(true).build();
    }

    // ────────────── LOGIN ──────────────
    @Nested @DisplayName("login()")
    class Login {

        @Test @DisplayName("Genera token cuando las credenciales son correctas")
        void loginExitoso() {
            when(usuarioRepository.findByEmail("carla@vetnova.cl")).thenReturn(Optional.of(usuarioActivo));
            when(passwordEncoder.matches("clave123", "hash-guardado")).thenReturn(true);
            when(credencialRepository.save(any(Credencial.class))).thenAnswer(inv -> inv.getArgument(0));

            LoginResponse response = authService.login(new LoginRequest("carla@vetnova.cl", "clave123"));

            assertThat(response.getToken()).isNotBlank();
            assertThat(response.getUsuarioId()).isEqualTo(1L);
            ArgumentCaptor<Credencial> cap = ArgumentCaptor.forClass(Credencial.class);
            verify(credencialRepository).save(cap.capture());
            assertThat(cap.getValue().getActiva()).isTrue();
        }

        @Test @DisplayName("Lanza excepción si el email no existe")
        void loginEmailInexistente() {
            when(usuarioRepository.findByEmail("x@y.cl")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> authService.login(new LoginRequest("x@y.cl", "clave")))
                    .isInstanceOf(CredencialesInvalidasException.class);
            verify(credencialRepository, never()).save(any());
        }

        @Test @DisplayName("Lanza excepción si el usuario está desactivado")
        void loginUsuarioDesactivado() {
            usuarioActivo.setActivo(false);
            when(usuarioRepository.findByEmail("carla@vetnova.cl")).thenReturn(Optional.of(usuarioActivo));
            assertThatThrownBy(() -> authService.login(new LoginRequest("carla@vetnova.cl", "clave")))
                    .isInstanceOf(CredencialesInvalidasException.class)
                    .hasMessageContaining("desactivado");
            verify(passwordEncoder, never()).matches(any(), any());
        }

        @Test @DisplayName("Lanza excepción si la contraseña es incorrecta")
        void loginPasswordIncorrecta() {
            when(usuarioRepository.findByEmail("carla@vetnova.cl")).thenReturn(Optional.of(usuarioActivo));
            when(passwordEncoder.matches("mala", "hash-guardado")).thenReturn(false);
            assertThatThrownBy(() -> authService.login(new LoginRequest("carla@vetnova.cl", "mala")))
                    .isInstanceOf(CredencialesInvalidasException.class);
        }
    }

    // ────────────── LOGOUT ──────────────
    @Nested @DisplayName("logout()")
    class Logout {

        @Test @DisplayName("Invalida la credencial al hacer logout")
        void logoutExitoso() {
            Credencial cred = Credencial.builder().token("tok").activa(true).build();
            when(credencialRepository.findByToken("tok")).thenReturn(Optional.of(cred));
            authService.logout("tok");
            assertThat(cred.getActiva()).isFalse();
            verify(credencialRepository).save(cred);
        }

        @Test @DisplayName("Lanza excepción si el token no existe")
        void logoutTokenInexistente() {
            when(credencialRepository.findByToken("x")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> authService.logout("x"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ────────────── VALIDAR TOKEN ──────────────
    @Nested @DisplayName("validarToken()")
    class ValidarToken {

        @Test @DisplayName("Retorna válido para un token activo no expirado")
        void tokenValido() {
            Credencial cred = Credencial.builder().usuarioId(1L).token("tok")
                    .activa(true).fechaExpiracion(LocalDateTime.now().plusHours(1)).build();
            when(credencialRepository.findByTokenAndActivaTrue("tok")).thenReturn(Optional.of(cred));
            ValidacionTokenResponse resp = authService.validarToken("tok");
            assertThat(resp.isValido()).isTrue();
            assertThat(resp.getUsuarioId()).isEqualTo(1L);
        }

        @Test @DisplayName("Retorna inválido para un token expirado")
        void tokenExpirado() {
            Credencial cred = Credencial.builder().token("viejo").activa(true)
                    .fechaExpiracion(LocalDateTime.now().minusHours(1)).build();
            when(credencialRepository.findByTokenAndActivaTrue("viejo")).thenReturn(Optional.of(cred));
            assertThat(authService.validarToken("viejo").isValido()).isFalse();
        }

        @Test @DisplayName("Retorna inválido si el token no existe en BD")
        void tokenNoExiste() {
            when(credencialRepository.findByTokenAndActivaTrue("no")).thenReturn(Optional.empty());
            assertThat(authService.validarToken("no").isValido()).isFalse();
        }
    }

    // ────────────── OBTENER USUARIO POR TOKEN ──────────────
    @Nested @DisplayName("obtenerUsuarioPorToken()")
    class ObtenerUsuarioPorToken {

        @Test @DisplayName("Retorna los datos del usuario cuando el token es válido")
        void retornaUsuario() {
            Credencial cred = Credencial.builder().usuarioId(1L).token("tok")
                    .activa(true).fechaExpiracion(LocalDateTime.now().plusHours(1)).build();
            when(credencialRepository.findByTokenAndActivaTrue("tok")).thenReturn(Optional.of(cred));
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioActivo));

            UsuarioResponse resp = authService.obtenerUsuarioPorToken("tok");

            assertThat(resp.getNombre()).isEqualTo("Carla");
            assertThat(resp.getRol()).isEqualTo(Rol.RECEPCIONISTA);
        }

        @Test @DisplayName("Lanza excepción si el token es inválido o expirado")
        void lanzaExcepcionTokenInvalido() {
            when(credencialRepository.findByTokenAndActivaTrue("bad")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> authService.obtenerUsuarioPorToken("bad"))
                    .isInstanceOf(CredencialesInvalidasException.class);
        }

        @Test @DisplayName("Lanza excepción si el token expiró (filter falla)")
        void lanzaExcepcionTokenExpirado() {
            Credencial cred = Credencial.builder().usuarioId(1L).token("exp")
                    .activa(true).fechaExpiracion(LocalDateTime.now().minusMinutes(1)).build();
            when(credencialRepository.findByTokenAndActivaTrue("exp")).thenReturn(Optional.of(cred));
            assertThatThrownBy(() -> authService.obtenerUsuarioPorToken("exp"))
                    .isInstanceOf(CredencialesInvalidasException.class);
        }

        @Test @DisplayName("Lanza excepción si no existe el usuario asociado al token")
        void lanzaExcepcionUsuarioNoEncontrado() {
            Credencial cred = Credencial.builder().usuarioId(99L).token("tok")
                    .activa(true).fechaExpiracion(LocalDateTime.now().plusHours(1)).build();
            when(credencialRepository.findByTokenAndActivaTrue("tok")).thenReturn(Optional.of(cred));
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> authService.obtenerUsuarioPorToken("tok"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
