package vetnova.autentificacion.service;

import vetnova.autentificacion.dto.CambiarPasswordRequest;
import vetnova.autentificacion.dto.CambiarRolRequest;
import vetnova.autentificacion.dto.UsuarioRequest;
import vetnova.autentificacion.dto.UsuarioResponse;
import vetnova.autentificacion.model.Rol;

import java.util.List;

public interface UsuarioService {

    UsuarioResponse crear(UsuarioRequest request);

    List<UsuarioResponse> listarTodos();

    UsuarioResponse obtenerPorId(Long id);

    List<UsuarioResponse> listarPorRol(Rol rol);

    UsuarioResponse actualizar(Long id, UsuarioRequest request);

    UsuarioResponse desactivar(Long id);

    UsuarioResponse activar(Long id);

    UsuarioResponse cambiarRol(Long id, CambiarRolRequest request);

    void cambiarPassword(Long id, CambiarPasswordRequest request);
}
