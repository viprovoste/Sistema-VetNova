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
@DisplayName("UsuarioService - pruebas unitarias con 100% de cobertura")
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario veterinario;
    private UsuarioRequest requestVet;

    @BeforeEach
    void setUp() {
        veterinario = Usuario.builder()
                .id(1L).nombre("Pablo").apellido("Soto")
                .email("pablo@vetnova.cl").passwordHash("hash")
                .rut("11111111-1").telefono("+569").rol(Rol.VETERINARIO)
                .activo(true).sucursalId(1L).build();

        requestVet = new UsuarioRequest("Pablo", "Soto", "pablo@vetnova.cl",
                "clave123", "11111111-1", "+569", Rol.VETERINARIO, 1L);
    }

    // ────────────── CREAR ──────────────
    @Nested @DisplayName("crear()")
    class Crear {

        @Test @DisplayName("Crea el usuario cuando el email no existe")
        void creaCorrectamente() {
            when(usuarioRepository.existsByEmail("pablo@vetnova.cl")).thenReturn(false);
            when(passwordEncoder.encode("clave123")).thenReturn("hash");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(veterinario);

            UsuarioResponse resp = usuarioService.crear(requestVet);

            assertThat(resp.getEmail()).isEqualTo("pablo@vetnova.cl");
            assertThat(resp.getRol()).isEqualTo(Rol.VETERINARIO);
        }

        @Test @DisplayName("Lanza excepción si el email ya está registrado")
        void lanzaExcepcionEmailDuplicado() {
            when(usuarioRepository.existsByEmail("pablo@vetnova.cl")).thenReturn(true);
            assertThatThrownBy(() -> usuarioService.crear(requestVet))
                    .isInstanceOf(EmailDuplicadoException.class);
            verify(usuarioRepository, never()).save(any());
        }
    }

    // ────────────── LISTAR ──────────────
    @Nested @DisplayName("listarTodos() / listarPorRol()")
    class Listar {

        @Test @DisplayName("Devuelve todos los usuarios")
        void listarTodos() {
            when(usuarioRepository.findAll()).thenReturn(List.of(veterinario));
            assertThat(usuarioService.listarTodos()).hasSize(1);
        }

        @Test @DisplayName("Devuelve usuarios filtrados por rol")
        void listarPorRol() {
            when(usuarioRepository.findByRol(Rol.VETERINARIO)).thenReturn(List.of(veterinario));
            List<UsuarioResponse> resultado = usuarioService.listarPorRol(Rol.VETERINARIO);
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getRol()).isEqualTo(Rol.VETERINARIO);
        }
    }

    // ────────────── OBTENER POR ID ──────────────
    @Nested @DisplayName("obtenerPorId()")
    class ObtenerPorId {

        @Test @DisplayName("Retorna el usuario cuando existe")
        void usuarioExiste() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
            assertThat(usuarioService.obtenerPorId(1L).getNombre()).isEqualTo("Pablo");
        }

        @Test @DisplayName("Lanza excepción cuando el id no existe")
        void usuarioNoExiste() {
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> usuarioService.obtenerPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ────────────── ACTUALIZAR ──────────────
    @Nested @DisplayName("actualizar()")
    class Actualizar {

        @Test @DisplayName("Actualiza manteniendo el mismo email (no verifica duplicado)")
        void actualizaMismoEmail() {
            // El email en el request es IGUAL al que ya tiene → no debe llamar existsByEmail
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
            when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            UsuarioResponse resp = usuarioService.actualizar(1L, requestVet);

            assertThat(resp.getNombre()).isEqualTo("Pablo");
            verify(usuarioRepository, never()).existsByEmail(any());
        }

        @Test @DisplayName("Actualiza con email distinto que no está en uso")
        void actualizaEmailDistintoLibre() {
            UsuarioRequest requestNuevoEmail = new UsuarioRequest("Pablo", "Soto", "nuevo@vetnova.cl",
                    "clave123", "11111111-1", "+569", Rol.VETERINARIO, 1L);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
            when(usuarioRepository.existsByEmail("nuevo@vetnova.cl")).thenReturn(false);
            when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            UsuarioResponse resp = usuarioService.actualizar(1L, requestNuevoEmail);

            assertThat(resp.getEmail()).isEqualTo("nuevo@vetnova.cl");
        }

        @Test @DisplayName("Lanza excepción si el nuevo email ya pertenece a otro usuario")
        void lanzaExcepcionEmailDuplicado() {
            UsuarioRequest requestConflicto = new UsuarioRequest("Pablo", "Soto", "ocupado@vetnova.cl",
                    "clave", "11111111-1", "+569", Rol.VETERINARIO, 1L);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
            when(usuarioRepository.existsByEmail("ocupado@vetnova.cl")).thenReturn(true);

            assertThatThrownBy(() -> usuarioService.actualizar(1L, requestConflicto))
                    .isInstanceOf(EmailDuplicadoException.class);
            verify(usuarioRepository, never()).save(any());
        }
    }

    // ────────────── ACTIVAR / DESACTIVAR ──────────────
    @Nested @DisplayName("activar() / desactivar()")
    class EstadoUsuario {

        @Test @DisplayName("Desactiva a un usuario (soft delete)")
        void desactiva() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
            when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            assertThat(usuarioService.desactivar(1L).getActivo()).isFalse();
        }

        @Test @DisplayName("Reactiva a un usuario desactivado")
        void activa() {
            veterinario.setActivo(false);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
            when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            assertThat(usuarioService.activar(1L).getActivo()).isTrue();
        }
    }

    // ────────────── CAMBIAR ROL ──────────────
    @Nested @DisplayName("cambiarRol()")
    class CambiarRol {

        @Test @DisplayName("Actualiza el rol del usuario")
        void cambiaRol() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
            when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            assertThat(usuarioService.cambiarRol(1L, new CambiarRolRequest(Rol.ADMIN_SUCURSAL)).getRol())
                    .isEqualTo(Rol.ADMIN_SUCURSAL);
        }
    }

    // ────────────── CAMBIAR PASSWORD ──────────────
    @Nested @DisplayName("cambiarPassword()")
    class CambiarPassword {

        @Test @DisplayName("Cambia la contraseña cuando la actual es correcta")
        void cambiaPasswordCorrectamente() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
            when(passwordEncoder.matches("actual", "hash")).thenReturn(true);
            when(passwordEncoder.encode("nueva")).thenReturn("nuevo-hash");

            usuarioService.cambiarPassword(1L, new CambiarPasswordRequest("actual", "nueva"));

            assertThat(veterinario.getPasswordHash()).isEqualTo("nuevo-hash");
            verify(usuarioRepository).save(veterinario);
        }

        @Test @DisplayName("Lanza excepción si la contraseña actual es incorrecta")
        void lanzaExcepcionPasswordIncorrecta() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            assertThatThrownBy(() -> usuarioService.cambiarPassword(1L, new CambiarPasswordRequest("mala", "nueva")))
                    .isInstanceOf(CredencialesInvalidasException.class);
            verify(usuarioRepository, never()).save(any());
        }
    }
}
