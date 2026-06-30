package vetnova.autentificacion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vetnova.autentificacion.dto.CambiarPasswordRequest;
import vetnova.autentificacion.dto.CambiarRolRequest;
import vetnova.autentificacion.dto.UsuarioRequest;
import vetnova.autentificacion.dto.UsuarioResponse;
import vetnova.autentificacion.model.Rol;
import vetnova.autentificacion.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema (clientes y personal interno)")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario (cliente web o personal interno)")
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios registrados")
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID",
            description = "Endpoint también consumido internamente por Inventario y Ventas")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @GetMapping("/rol/{rol}")
    @Operation(summary = "Listar usuarios por rol")
    public ResponseEntity<List<UsuarioResponse>> listarPorRol(@PathVariable Rol rol) {
        return ResponseEntity.ok(usuarioService.listarPorRol(rol));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un usuario")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar usuario", description = "Soft delete: nunca se elimina un usuario con historial")
    public ResponseEntity<UsuarioResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.desactivar(id));
    }

    @PatchMapping("/{id}/activar")
    @Operation(summary = "Reactivar usuario")
    public ResponseEntity<UsuarioResponse> activar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.activar(id));
    }

    @PutMapping("/{id}/rol")
    @Operation(summary = "Cambiar el rol de un usuario", description = "Solo debería usarlo ADMIN_SISTEMA")
    public ResponseEntity<UsuarioResponse> cambiarRol(@PathVariable Long id,
                                                        @Valid @RequestBody CambiarRolRequest request) {
        return ResponseEntity.ok(usuarioService.cambiarRol(id, request));
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Cambiar la contraseña de un usuario")
    public ResponseEntity<Void> cambiarPassword(@PathVariable Long id, @Valid @RequestBody CambiarPasswordRequest request) {
        usuarioService.cambiarPassword(id, request);
        return ResponseEntity.noContent().build();
    }
}
