package cl.duoc.login.dto.request;

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
public class DtoAuthRequest {

    // Valida que username no sea null ni vacío
    @NotBlank(message = "El username es obligatorio")
    private String username;

    // Valida que password no sea null ni vacío
    @NotBlank(message = "El password es obligatorio")
    private String password;
}
