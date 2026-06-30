package vetnova.autentificacion.service;

import vetnova.autentificacion.dto.LoginRequest;
import vetnova.autentificacion.dto.LoginResponse;
import vetnova.autentificacion.dto.UsuarioResponse;
import vetnova.autentificacion.dto.ValidacionTokenResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void logout(String token);

    ValidacionTokenResponse validarToken(String token);

    UsuarioResponse obtenerUsuarioPorToken(String token);
}
