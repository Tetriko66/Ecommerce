package com.Fullstack.Pago.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response {
    private Long id;
    private Long usuarioId;
    private Long pedidoId;
    private Double monto;
    private LocalDateTime fechaCreacion;
}