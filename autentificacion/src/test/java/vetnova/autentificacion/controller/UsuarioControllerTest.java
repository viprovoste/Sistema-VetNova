package vetnova.autentificacion.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vetnova.autentificacion.dto.*;
import vetnova.autentificacion.model.Rol;
import vetnova.autentificacion.service.UsuarioService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioController - pruebas unitarias")
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    private UsuarioController controller;

    private UsuarioResponse usuarioResponse;

    @BeforeEach
    void setUp() {
        controller = new UsuarioController(usuarioService);
        usuarioResponse = UsuarioResponse.builder().id(1L).nombre("Pablo").rol(Rol.VETERINARIO).activo(true).build();
    }

    @Test
    @DisplayName("crear() responde 201 CREATED")
    void crearResponde201() {
        UsuarioRequest request = new UsuarioRequest("Pablo", "Soto", "pablo@vetnova.cl", "clave123",
                "11111111-1", "+56911111111", Rol.VETERINARIO, 1L);
        when(usuarioService.crear(request)).thenReturn(usuarioResponse);

        ResponseEntity<UsuarioResponse> response = controller.crear(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("listarTodos() responde 200")
    void listarTodos() {
        when(usuarioService.listarTodos()).thenReturn(List.of(usuarioResponse));

        ResponseEntity<List<UsuarioResponse>> response = controller.listarTodos();

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorId() delega en el servicio")
    void obtenerPorId() {
        when(usuarioService.obtenerPorId(1L)).thenReturn(usuarioResponse);

        controller.obtenerPorId(1L);

        verify(usuarioService).obtenerPorId(1L);
    }

    @Test
    @DisplayName("listarPorRol() delega con el filtro correcto")
    void listarPorRol() {
        when(usuarioService.listarPorRol(Rol.VETERINARIO)).thenReturn(List.of(usuarioResponse));

        ResponseEntity<List<UsuarioResponse>> response = controller.listarPorRol(Rol.VETERINARIO);

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("actualizar() delega en el servicio")
    void actualizar() {
        UsuarioRequest request = new UsuarioRequest("Pablo", "Soto", "pablo@vetnova.cl", "clave123",
                "11111111-1", "+56911111111", Rol.VETERINARIO, 1L);
        when(usuarioService.actualizar(1L, request)).thenReturn(usuarioResponse);

        ResponseEntity<UsuarioResponse> response = controller.actualizar(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("desactivar() delega en el servicio")
    void desactivar() {
        when(usuarioService.desactivar(1L)).thenReturn(usuarioResponse);

        controller.desactivar(1L);

        verify(usuarioService).desactivar(1L);
    }

    @Test
    @DisplayName("activar() delega en el servicio")
    void activar() {
        when(usuarioService.activar(1L)).thenReturn(usuarioResponse);

        controller.activar(1L);

        verify(usuarioService).activar(1L);
    }

    @Test
    @DisplayName("cambiarRol() delega en el servicio")
    void cambiarRol() {
        CambiarRolRequest request = new CambiarRolRequest(Rol.ADMIN_SUCURSAL);
        when(usuarioService.cambiarRol(1L, request)).thenReturn(usuarioResponse);

        ResponseEntity<UsuarioResponse> response = controller.cambiarRol(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("cambiarPassword() delega en el servicio y responde 204 NO_CONTENT")
    void cambiarPassword() {
        CambiarPasswordRequest request = new CambiarPasswordRequest("actual", "nuevaClave");

        ResponseEntity<Void> response = controller.cambiarPassword(1L, request);

        verify(usuarioService).cambiarPassword(1L, request);
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }
}
