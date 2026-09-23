package cl.duoc.login.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Genera getters, setters, toString, equals y hashCode
@Data

// Genera constructor con todos los atributos
@AllArgsConstructor

// Genera constructor vacío
@NoArgsConstructor
@Schema(description = "Entidad que representa los datos del login")
public class DtoAuthRequest {

    // Valida que username no sea null ni vacío
    @NotBlank(message = "El username es obligatorio")
    @Schema(description = "Nombre de usuario", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    // Valida que password no sea null ni vacío
    @NotBlank(message = "El password es obligatorio")
    @Schema(description = "Password de usuario", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
