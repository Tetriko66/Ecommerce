package cl.duoc.login.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Genera getters, setters, toString, equals y hashCode
@Data

// Genera constructor con todos los atributos
@AllArgsConstructor

// Genera constructor vacío
@NoArgsConstructor
@Schema(description = "Entidad que representa los datos del response con el token")
public class DtoAuthResponse {

    // Almacena el token JWT que será devuelto al cliente
    @Schema(description = "Token JWT", example = "esdfa687asd5hasd...")
    private String token;
}
