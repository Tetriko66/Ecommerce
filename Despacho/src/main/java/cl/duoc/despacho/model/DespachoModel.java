package cl.duoc.despacho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "despacho")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DespachoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_despacho")
    private Long idDespacho;

    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Column(name = "direccion_entrega", nullable = false)
    private String direccionEntrega;

    // PREPARANDO, EN_CAMINO, ENTREGADO
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_despacho", nullable = false)
    private LocalDateTime fechaDespacho;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;
}
