package com.Fullstack.Carro.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Response {
    private Long id;
    private Long usuarioId;
    private Long productoId;
    private Integer cantidad;
    private LocalDateTime fechaCreacion;
}
