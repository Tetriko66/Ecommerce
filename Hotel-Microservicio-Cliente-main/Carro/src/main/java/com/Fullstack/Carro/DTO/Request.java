package com.Fullstack.Carro.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Request {
    @NotNull
    private Long usuarioId;

    @NotNull
    private Long productoId;

    @NotNull
    private Integer cantidad;
}