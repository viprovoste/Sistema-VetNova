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
@DisplayName("AuthService - pruebas unitarias")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CredencialRepository credencialRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private Usuario usuarioActivo;

    @BeforeEach
    void setUp() {
        usuarioActivo = Usuario.builder()
                .id(1L)
                .nombre("Carla")
                .email("carla@vetnova.cl")
                .passwordHash("hash-guardado")
                .rol(Rol.RECEPCIONISTA)
                .activo(true)
                .build();
    }

    @Nested
    @DisplayName("Login")
    class Login {

        @Test
        @DisplayName("Genera un token cuando las credenciales son correctas")
        void loginExitoso() {
            LoginRequest request = new LoginRequest("carla@vetnova.cl", "clave123");

            when(usuarioRepository.findByEmail("carla@vetnova.cl")).thenReturn(Optional.of(usuarioActivo));
            when(passwordEncoder.matches("clave123", "hash-guardado")).thenReturn(true);
            when(credencialRepository.save(any(Credencial.class))).thenAnswer(inv -> inv.getArgument(0));

            LoginResponse response = authService.login(request);

            assertThat(response.getToken()).isNotBlank();
            assertThat(response.getUsuarioId()).isEqualTo(1L);
            assertThat(response.getRol()).isEqualTo(Rol.RECEPCIONISTA);

            ArgumentCaptor<Credencial> captor = ArgumentCaptor.forClass(Credencial.class);
            verify(credencialRepository).save(captor.capture());
            assertThat(captor.getValue().getUsuarioId()).isEqualTo(1L);
            assertThat(captor.getValue().getActiva()).isTrue();
        }

        @Test
        @DisplayName("Rechaza el login si el email no existe")
        void loginFallaPorEmailInexistente() {
            LoginRequest request = new LoginRequest("noexiste@vetnova.cl", "clave123");
            when(usuarioRepository.findByEmail("noexiste@vetnova.cl")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CredencialesInvalidasException.class);

            verify(credencialRepository, never()).save(any());
        }

        @Test
        @DisplayName("Rechaza el login si la contraseña es incorrecta")
        void loginFallaPorPasswordIncorrecta() {
            LoginRequest request = new LoginRequest("carla@vetnova.cl", "claveMala");
            when(usuarioRepository.findByEmail("carla@vetnova.cl")).thenReturn(Optional.of(usuarioActivo));
            when(passwordEncoder.matches("claveMala", "hash-guardado")).thenReturn(false);

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CredencialesInvalidasException.class);
        }

        @Test
        @DisplayName("Rechaza el login si el usuario está desactivado")
        void loginFallaPorUsuarioDesactivado() {
            usuarioActivo.setActivo(false);
            LoginRequest request = new LoginRequest("carla@vetnova.cl", "clave123");
            when(usuarioRepository.findByEmail("carla@vetnova.cl")).thenReturn(Optional.of(usuarioActivo));

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CredencialesInvalidasException.class)
                    .hasMessageContaining("desactivado");

            verify(passwordEncoder, never()).matches(any(), any());
        }
    }

    @Nested
    @DisplayName("Validación de token (consumido por Inventario y Ventas)")
    class ValidacionToken {

        @Test
        @DisplayName("Marca como válido un token activo y no expirado")
        void tokenValido() {
            Credencial credencial = Credencial.builder()
                    .usuarioId(1L)
                    .token("token-abc")
                    .activa(true)
                    .fechaExpiracion(LocalDateTime.now().plusHours(1))
                    .build();

            when(credencialRepository.findByTokenAndActivaTrue("token-abc")).thenReturn(Optional.of(credencial));

            ValidacionTokenResponse response = authService.validarToken("token-abc");

            assertThat(response.isValido()).isTrue();
            assertThat(response.getUsuarioId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Marca como inválido un token expirado")
        void tokenExpirado() {
            Credencial credencial = Credencial.builder()
                    .usuarioId(1L)
                    .token("token-viejo")
                    .activa(true)
                    .fechaExpiracion(LocalDateTime.now().minusHours(1))
                    .build();

            when(credencialRepository.findByTokenAndActivaTrue("token-viejo")).thenReturn(Optional.of(credencial));

            ValidacionTokenResponse response = authService.validarToken("token-viejo");

            assertThat(response.isValido()).isFalse();
        }

        @Test
        @DisplayName("Marca como inválido un token inexistente")
        void tokenInexistente() {
            when(credencialRepository.findByTokenAndActivaTrue("token-fantasma")).thenReturn(Optional.empty());

            ValidacionTokenResponse response = authService.validarToken("token-fantasma");

            assertThat(response.isValido()).isFalse();
        }
    }

    @Nested
    @DisplayName("Logout")
    class Logout {

        @Test
        @DisplayName("Invalida la credencial al hacer logout")
        void logoutExitoso() {
            Credencial credencial = Credencial.builder().token("token-abc").activa(true).build();
            when(credencialRepository.findByToken("token-abc")).thenReturn(Optional.of(credencial));

            authService.logout("token-abc");

            assertThat(credencial.getActiva()).isFalse();
            verify(credencialRepository).save(credencial);
        }

        @Test
        @DisplayName("Lanza excepción si el token no existe")
        void logoutFallaSiTokenNoExiste() {
            when(credencialRepository.findByToken("token-x")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.logout("token-x"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
