package cl.duoc.inventario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoInventarioResponse {

    private Long idInventario;
    private Long productoId;
    private Integer cantidad;
    private Integer stockMinimo;
    private Boolean stockBajo;
    private LocalDateTime fechaActualizacion;
}
