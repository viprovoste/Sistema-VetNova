package vetnova.ventas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVentaRequest {

    @NotNull
    private Long productoId;

    @NotNull
    @Positive
    private Integer cantidad;
}
