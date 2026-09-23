package cl.duoc.MicroServicioNotificaciones.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Indica que esta clase representa una tabla en la BD
@Entity

// Indica el nombre exacto de la tabla
@Table(name = "notificacion")

// Genera getters, setters, toString, equals y hashCode
@Data

// Genera constructor vacío
@NoArgsConstructor

// Genera constructor con todos los atributos
@AllArgsConstructor
public class NotificacionModel {

    // Indica que este atributo es la clave primaria
    @Id

    // Indica que el ID se generará automáticamente
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // Mapea este atributo con la columna id_notificacion
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    // Tipo de notificación: EMAIL, SMS o PUSH
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoNotificacion tipo;

    // Destinatario (email o número de teléfono)
    @Column(name = "destinatario", nullable = false)
    private String destinatario;

    // Asunto del mensaje
    @Column(name = "asunto", nullable = false)
    private String asunto;

    // Cuerpo del mensaje
    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    // Estado actual de la notificación
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoNotificacion estado;

    // Fecha en que se creó la notificación
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // Fecha en que se envió (puede ser null si aún no se envió)
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;
}
