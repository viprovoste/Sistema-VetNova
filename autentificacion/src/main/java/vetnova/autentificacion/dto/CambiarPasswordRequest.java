package vetnova.autentificacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarPasswordRequest {

    @NotBlank
    private String passwordActual;

    @NotBlank
    @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
    private String passwordNueva;
}
