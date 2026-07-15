package vetnova.autentificacion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vetnova.autentificacion.dto.LoginRequest;
import vetnova.autentificacion.dto.LoginResponse;
import vetnova.autentificacion.dto.UsuarioResponse;
import vetnova.autentificacion.dto.ValidacionTokenResponse;
import vetnova.autentificacion.exception.CredencialesInvalidasException;
import vetnova.autentificacion.exception.ResourceNotFoundException;
import vetnova.autentificacion.model.Credencial;
import vetnova.autentificacion.model.Usuario;
import vetnova.autentificacion.repository.CredencialRepository;
import vetnova.autentificacion.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int HORAS_EXPIRACION = 12;

    private final UsuarioRepository usuarioRepository;
    private final CredencialRepository credencialRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CredencialesInvalidasException("Email o contraseña incorrectos"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new CredencialesInvalidasException("El usuario se encuentra desactivado");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException("Email o contraseña incorrectos");
        }

        String token = UUID.randomUUID().toString();
        Credencial credencial = Credencial.builder()
                .usuarioId(usuario.getId())
                .token(token)
                .fechaCreacion(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusHours(HORAS_EXPIRACION))
                .activa(true)
                .build();
        credencialRepository.save(credencial);

        return LoginResponse.builder()
                .token(token)
                .usuarioId(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build();
    }

    @Transactional
    public void logout(String token) {
        Credencial credencial = credencialRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token no encontrado"));
        credencial.setActiva(false);
        credencialRepository.save(credencial);
    }

    public ValidacionTokenResponse validarToken(String token) {
        return credencialRepository.findByTokenAndActivaTrue(token)
                .filter(c -> c.getFechaExpiracion().isAfter(LocalDateTime.now()))
                .map(c -> ValidacionTokenResponse.builder().valido(true).usuarioId(c.getUsuarioId()).build())
                .orElse(ValidacionTokenResponse.builder().valido(false).usuarioId(null).build());
    }

    public UsuarioResponse obtenerUsuarioPorToken(String token) {
        Credencial credencial = credencialRepository.findByTokenAndActivaTrue(token)
                .filter(c -> c.getFechaExpiracion().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new CredencialesInvalidasException("El token no es válido o ha expirado"));

        Usuario usuario = usuarioRepository.findById(credencial.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario asociado al token"));

        return mapearResponse(usuario);
    }

    private UsuarioResponse mapearResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .rut(usuario.getRut())
                .telefono(usuario.getTelefono())
                .rol(usuario.getRol())
                .activo(usuario.getActivo())
                .sucursalId(usuario.getSucursalId())
                .fechaCreacion(usuario.getFechaCreacion())
                .build();
    }
}
