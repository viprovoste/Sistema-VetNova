package vetnova.ventas.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionTokenDTO {
    private boolean valido;
    private Long usuarioId;
}
