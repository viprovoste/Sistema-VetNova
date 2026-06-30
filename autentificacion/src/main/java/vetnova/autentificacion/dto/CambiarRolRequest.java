package vetnova.autentificacion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vetnova.autentificacion.model.Rol;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarRolRequest {

    @NotNull
    private Rol rol;
}
