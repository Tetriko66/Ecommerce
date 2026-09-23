package com.Fullstack.Pago.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;
    private Long pedidoId;
    private Double monto;
    private String metodoPago;
    private LocalDateTime fechaPago;
}
