package com.Fullstack.Pago.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
    @NotNull
    private Long usuarioId;

    @NotNull
    private Long pedidoId;

    @NotNull
    private Double monto;
}
