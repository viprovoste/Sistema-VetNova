package vetnova.ventas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmitirFacturaRequest {

    @NotBlank(message = "La razón social es obligatoria para emitir una factura")
    private String razonSocial;

    private String rutEmpresa;
}
