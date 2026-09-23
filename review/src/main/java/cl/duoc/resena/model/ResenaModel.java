package cl.duoc.resena.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "resena")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResenaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resena")
    private Long idResena;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    // Calificación del 1 al 5
    @Column(name = "calificacion", nullable = false)
    private Integer calificacion;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_resena", nullable = false)
    private LocalDateTime fechaResena;
}
