package cl.duoc.MicroServicioNotificaciones.dto.request;

import cl.duoc.MicroServicioNotificaciones.model.TipoNotificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Genera getters, setters, toString, equals y hashCode
@Data

// Genera constructor con todos los atributos
@AllArgsConstructor

// Genera constructor vacío
@NoArgsConstructor
public class DtoNotificacionRequest {

    // Valida que el tipo no sea null
    @NotNull(message = "El tipo de notificación es obligatorio")
    private TipoNotificacion tipo;

    // Valida que el destinatario no sea null ni vacío
    @NotBlank(message = "El destinatario es obligatorio")
    private String destinatario;

    // Valida que el asunto no sea null ni vacío
    @NotBlank(message = "El asunto es obligatorio")
    private String asunto;

    // Valida que el mensaje no sea null ni vacío
    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;
}
