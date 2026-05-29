package com.vetnova.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vetnova.auth.dto.AuthResponse;
import com.vetnova.auth.dto.LoginRequest;
import com.vetnova.auth.dto.RegistroRequest;
import com.vetnova.auth.model.Usuario;
import com.vetnova.auth.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // <--- encriptador

    public String registrarUsuario(RegistroRequest request) {
        if (usuarioRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está registrado.");
        }

        Usuario nuevoUsuario = Usuario.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password())) 
                .nombre(request.nombre())
                .apellido(request.apellido())
                .rol(request.rol())
                .build();

        usuarioRepository.save(nuevoUsuario);
        return "Usuario registrado exitosamente con el rol de " + request.rol();
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta.");
        }

        String tokenRealJWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... (Token generado para " + usuario.getRol() + ")";

        return new AuthResponse(
                tokenRealJWT,
                usuario.getUsername(),
                usuario.getRol().name()
        );
    }
}
