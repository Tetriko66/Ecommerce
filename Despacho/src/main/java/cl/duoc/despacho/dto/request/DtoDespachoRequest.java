package cl.duoc.despacho.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoDespachoRequest {

    @NotNull(message = "El id de pedido es obligatorio")
    private Long pedidoId;

    @NotBlank(message = "La dirección de entrega es obligatoria")
    private String direccionEntrega;
}
