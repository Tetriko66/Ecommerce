package cl.duoc.pedido.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "monto_total", nullable = false)
    private Double montoTotal;

    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago;

    // PENDIENTE, PAGADO, CANCELADO
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;
}
