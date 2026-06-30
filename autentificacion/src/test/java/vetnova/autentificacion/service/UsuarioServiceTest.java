package vetnova.autentificacion.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import vetnova.autentificacion.dto.CambiarPasswordRequest;
import vetnova.autentificacion.dto.CambiarRolRequest;
import vetnova.autentificacion.dto.UsuarioRequest;
import vetnova.autentificacion.dto.UsuarioResponse;
import vetnova.autentificacion.exception.CredencialesInvalidasException;
import vetnova.autentificacion.exception.EmailDuplicadoException;
import vetnova.autentificacion.exception.ResourceNotFoundException;
import vetnova.autentificacion.model.Rol;
import vetnova.autentificacion.model.Usuario;
import vetnova.autentificacion.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - pruebas unitarias")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuarioVeterinario;
    private UsuarioRequest requestVeterinario;

    @BeforeEach
    void setUp() {
        usuarioVeterinario = Usuario.builder()
                .id(1L)
                .nombre("Pablo")
                .apellido("Soto")
                .email("pablo.soto@vetnova.cl")
                .passwordHash("hash-encriptado")
                .rut("11111111-1")
                .telefono("+56911111111")
                .rol(Rol.VETERINARIO)
                .activo(true)
                .sucursalId(1L)
                .build();

        requestVeterinario = new UsuarioRequest();
        requestVeterinario.setNombre("Pablo");
        requestVeterinario.setApellido("Soto");
        requestVeterinario.setEmail("pablo.soto@vetnova.cl");
        requestVeterinario.setPassword("clave123");
        requestVeterinario.setRut("11111111-1");
        requestVeterinario.setTelefono("+56911111111");
        requestVeterinario.setRol(Rol.VETERINARIO);
        requestVeterinario.setSucursalId(1L);
    }

    @Nested
    @DisplayName("Creación de usuarios")
    class Crear {

        @Test
        @DisplayName("Crea un usuario correctamente cuando el email no existe")
        void creaUsuarioCorrectamente() {
            when(usuarioRepository.existsByEmail(requestVeterinario.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(requestVeterinario.getPassword())).thenReturn("hash-encriptado");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioVeterinario);

            UsuarioResponse response = usuarioService.crear(requestVeterinario);

            assertThat(response).isNotNull();
            assertThat(response.getEmail()).isEqualTo("pablo.soto@vetnova.cl");
            assertThat(response.getRol()).isEqualTo(Rol.VETERINARIO);
            verify(usuarioRepository).save(any(Usuario.class));
            verify(passwordEncoder).encode("clave123");
        }

        @Test
        @DisplayName("Lanza excepción si el email ya está registrado")
        void lanzaExcepcionSiEmailDuplicado() {
            when(usuarioRepository.existsByEmail(requestVeterinario.getEmail())).thenReturn(true);

            assertThatThrownBy(() -> usuarioService.crear(requestVeterinario))
                    .isInstanceOf(EmailDuplicadoException.class)
                    .hasMessageContaining(requestVeterinario.getEmail());

            verify(usuarioRepository, never()).save(any(Usuario.class));
        }
    }

    @Nested
    @DisplayName("Consultas")
    class Consultas {

        @Test
        @DisplayName("Obtiene un usuario existente por ID")
        void obtienePorIdExistente() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioVeterinario));

            UsuarioResponse response = usuarioService.obtenerPorId(1L);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getNombre()).isEqualTo("Pablo");
        }

        @Test
        @DisplayName("Lanza excepción si el usuario no existe")
        void lanzaExcepcionSiUsuarioNoExiste() {
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.obtenerPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Lista usuarios filtrando por rol")
        void listaUsuariosPorRol() {
            when(usuarioRepository.findByRol(Rol.VETERINARIO)).thenReturn(List.of(usuarioVeterinario));

            List<UsuarioResponse> resultado = usuarioService.listarPorRol(Rol.VETERINARIO);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getRol()).isEqualTo(Rol.VETERINARIO);
        }
    }

    @Nested
    @DisplayName("Activación y desactivación")
    class EstadoUsuario {

        @Test
        @DisplayName("Desactiva un usuario existente (soft delete)")
        void desactivaUsuario() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioVeterinario));
            when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

            UsuarioResponse response = usuarioService.desactivar(1L);

            assertThat(response.getActivo()).isFalse();
        }

        @Test
        @DisplayName("Reactiva un usuario existente")
        void activaUsuario() {
            usuarioVeterinario.setActivo(false);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioVeterinario));
            when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

            UsuarioResponse response = usuarioService.activar(1L);

            assertThat(response.getActivo()).isTrue();
        }
    }

    @Nested
    @DisplayName("Cambios sensibles: rol y contraseña")
    class CambiosSensibles {

        @Test
        @DisplayName("Cambia el rol de un usuario")
        void cambiaRol() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioVeterinario));
            when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

            CambiarRolRequest request = new CambiarRolRequest(Rol.ADMIN_SUCURSAL);
            UsuarioResponse response = usuarioService.cambiarRol(1L, request);

            assertThat(response.getRol()).isEqualTo(Rol.ADMIN_SUCURSAL);
        }

        @Test
        @DisplayName("Cambia la contraseña cuando la actual es correcta")
        void cambiaPasswordCorrectamente() {
            CambiarPasswordRequest request = new CambiarPasswordRequest("claveActual", "claveNueva123");

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioVeterinario));
            when(passwordEncoder.matches("claveActual", usuarioVeterinario.getPasswordHash())).thenReturn(true);
            when(passwordEncoder.encode("claveNueva123")).thenReturn("nuevo-hash");

            usuarioService.cambiarPassword(1L, request);

            verify(usuarioRepository).save(usuarioVeterinario);
            assertThat(usuarioVeterinario.getPasswordHash()).isEqualTo("nuevo-hash");
        }

        @Test
        @DisplayName("Lanza excepción si la contraseña actual es incorrecta")
        void lanzaExcepcionSiPasswordActualIncorrecta() {
            CambiarPasswordRequest request = new CambiarPasswordRequest("claveIncorrecta", "claveNueva123");

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioVeterinario));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            assertThatThrownBy(() -> usuarioService.cambiarPassword(1L, request))
                    .isInstanceOf(CredencialesInvalidasException.class);

            verify(usuarioRepository, never()).save(any(Usuario.class));
        }
    }
}
