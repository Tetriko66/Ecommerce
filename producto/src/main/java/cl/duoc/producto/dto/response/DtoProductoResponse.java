package cl.duoc.producto.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoProductoResponse {

    private Long idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String categoria;
    private Integer stock;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
