package cl.duoc.pedido.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoPedidoRequest {

    @NotNull(message = "El id de usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El id de producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotNull(message = "El monto total es obligatorio")
    private Double montoTotal;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;
}
