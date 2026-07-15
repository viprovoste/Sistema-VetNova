package vetnova.autentificacion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vetnova.autentificacion.dto.LoginRequest;
import vetnova.autentificacion.dto.LoginResponse;
import vetnova.autentificacion.dto.UsuarioResponse;
import vetnova.autentificacion.dto.ValidacionTokenResponse;
import vetnova.autentificacion.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación",
        description = "Login, logout y validación de tokens. Estos dos últimos endpoints son consumidos "
                + "por los microservicios de Inventario y Ventas antes de aceptar operaciones sensibles.")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida credenciales y devuelve un token de sesión")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Invalida el token enviado")
    public ResponseEntity<Void> logout(@RequestParam String token) {
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validar")
    @Operation(summary = "Validar token",
            description = "Usado por otros microservicios (Inventario, Ventas) para verificar si un token sigue siendo válido")
    public ResponseEntity<ValidacionTokenResponse> validar(
            @Parameter(description = "Token de sesión a validar") @RequestParam String token) {
        return ResponseEntity.ok(authService.validarToken(token));
    }

    @GetMapping("/usuario-por-token")
    @Operation(summary = "Obtener usuario autenticado",
            description = "Usado por otros microservicios para obtener los datos del usuario dueño del token (ej: RUT para boletas)")
    public ResponseEntity<UsuarioResponse> usuarioPorToken(@RequestParam String token) {
        return ResponseEntity.ok(authService.obtenerUsuarioPorToken(token));
    }
}
