package cl.duoc.despacho.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoDespachoResponse {

    private Long idDespacho;
    private Long pedidoId;
    private String direccionEntrega;
    private String estado;
    private LocalDateTime fechaDespacho;
    private LocalDateTime fechaEntrega;
}
