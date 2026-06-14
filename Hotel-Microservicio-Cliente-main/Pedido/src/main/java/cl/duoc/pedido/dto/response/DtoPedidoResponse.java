package cl.duoc.pedido.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoPedidoResponse {

    private Long idPedido;
    private Long usuarioId;
    private Long productoId;
    private Integer cantidad;
    private Double montoTotal;
    private String metodoPago;
    private String estado;
    private LocalDateTime fechaPedido;
}
