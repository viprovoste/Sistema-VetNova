package vetnova.ventas.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificarStockRequestDTO {
    private Long sucursalId;
    private List<ItemVerificacionStockDTO> items;
}
