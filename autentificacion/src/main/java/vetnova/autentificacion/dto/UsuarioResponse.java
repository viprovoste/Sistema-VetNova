package vetnova.autentificacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vetnova.autentificacion.model.Rol;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rut;
    private String telefono;
    private Rol rol;
    private Boolean activo;
    private Long sucursalId;
    private LocalDateTime fechaCreacion;
}
