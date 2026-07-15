package vetnova.autentificacion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailDuplicadoException("Ya existe un usuario registrado con el email: " + request.getEmail());
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .rut(request.getRut())
                .telefono(request.getTelefono())
                .rol(request.getRol())
                .sucursalId(request.getSucursalId())
                .activo(true)
                .build();

        return mapearResponse(usuarioRepository.save(usuario));
    }

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::mapearResponse).toList();
    }

    public UsuarioResponse obtenerPorId(Long id) {
        return mapearResponse(buscarPorId(id));
    }

    public List<UsuarioResponse> listarPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol).stream().map(this::mapearResponse).toList();
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscarPorId(id);

        if (!usuario.getEmail().equals(request.getEmail()) && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailDuplicadoException("Ya existe un usuario registrado con el email: " + request.getEmail());
        }

        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setRut(request.getRut());
        usuario.setTelefono(request.getTelefono());
        usuario.setSucursalId(request.getSucursalId());

        return mapearResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse desactivar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
        return mapearResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse activar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(true);
        return mapearResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse cambiarRol(Long id, CambiarRolRequest request) {
        Usuario usuario = buscarPorId(id);
        usuario.setRol(request.getRol());
        return mapearResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void cambiarPassword(Long id, CambiarPasswordRequest request) {
        Usuario usuario = buscarPorId(id);

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException("La contraseña actual no es correcta");
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);
    }

    private Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con id: " + id));
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
