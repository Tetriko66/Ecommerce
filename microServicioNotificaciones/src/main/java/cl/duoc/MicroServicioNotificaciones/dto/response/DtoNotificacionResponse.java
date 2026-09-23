package cl.duoc.MicroServicioNotificaciones.dto.response;

import cl.duoc.MicroServicioNotificaciones.model.EstadoNotificacion;
import cl.duoc.MicroServicioNotificaciones.model.TipoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Genera getters, setters, toString, equals y hashCode
@Data

// Genera constructor con todos los atributos
@AllArgsConstructor

// Genera constructor vacío
@NoArgsConstructor
public class DtoNotificacionResponse {

    private Long idNotificacion;
    private TipoNotificacion tipo;
    private String destinatario;
    private String asunto;
    private String mensaje;
    private EstadoNotificacion estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEnvio;
}
