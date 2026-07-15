package vetnova.autentificacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vetnova.autentificacion.model.Rol;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private Long usuarioId;
    private String nombre;
    private String email;
    private Rol rol;
}
