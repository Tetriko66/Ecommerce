package cl.duoc.resena.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoResenaResponse {

    private Long idResena;
    private Long usuarioId;
    private Long productoId;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fechaResena;
}
