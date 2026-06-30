package vetnova.ventas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vetnova.ventas.model.MetodoPago;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagarVentaRequest {

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;
}
