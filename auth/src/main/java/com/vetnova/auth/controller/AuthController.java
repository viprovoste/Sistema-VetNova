package com.vetnova.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vetnova.auth.dto.AuthResponse;
import com.vetnova.auth.dto.LoginRequest;
import com.vetnova.auth.dto.RegistroRequest;
import com.vetnova.auth.model.Rol;
import com.vetnova.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/registrar")
    public ResponseEntity<String> registrarGet(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam Rol rol) {
        RegistroRequest request = new RegistroRequest(username, password, nombre, apellido, rol);
        String mensaje = authService.registrarUsuario(request);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping("/login")
    public ResponseEntity<AuthResponse> loginGet(
            @RequestParam String username,
            @RequestParam String password) {
        LoginRequest request = new LoginRequest(username, password);
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@RequestBody RegistroRequest request) {
        String mensaje = authService.registrarUsuario(request);
        return ResponseEntity.ok(mensaje);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
