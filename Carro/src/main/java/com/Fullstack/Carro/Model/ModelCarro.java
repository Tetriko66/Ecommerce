package com.Fullstack.Carro.Model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "carrito")
@Getter
@Setter
public class ModelCarro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;
    private Long productoId;
    private Integer cantidad;

    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
