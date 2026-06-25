package cl.duoc.MicroServicioNotificaciones.controller;

import cl.duoc.MicroServicioNotificaciones.dto.request.DtoNotificacionRequest;
import cl.duoc.MicroServicioNotificaciones.dto.response.DtoNotificacionResponse;
import cl.duoc.MicroServicioNotificaciones.model.EstadoNotificacion;
import cl.duoc.MicroServicioNotificaciones.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificacion API", description = "Operaciones para crear y gestionar notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @Operation(description = "Obtiene todas las notificaciones registradas")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones obtenida exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoNotificacionResponse.class),
            examples = @ExampleObject(value = "[{\"idNotificacion\": 1, \"tipo\": \"EMAIL\", \"destinatario\": \"user@example.com\", \"estado\": \"PENDIENTE\"}]")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping
    public ResponseEntity<List<DtoNotificacionResponse>> obtenerTodas() {
        return ResponseEntity.ok(notificacionService.obtenerTodas());
    }

    @Operation(description = "Obtiene una notificación por su ID")
    @ApiResponse(responseCode = "200", description = "Notificación encontrada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoNotificacionResponse.class),
            examples = @ExampleObject(value = "{\"idNotificacion\": 1, \"tipo\": \"EMAIL\", \"destinatario\": \"user@example.com\", \"asunto\": \"Pedido confirmado\", \"estado\": \"PENDIENTE\"}")))
    @ApiResponse(responseCode = "404", description = "Notificación no encontrada",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"Notificación no encontrada con id: 99\"}")))
    @GetMapping("/{id}")
    public ResponseEntity<DtoNotificacionResponse> obtenerPorId(
            @Parameter(description = "ID de la notificación", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.obtenerPorId(id));
    }

    @Operation(description = "Obtiene notificaciones filtradas por estado (PENDIENTE, ENVIADO, FALLIDO)")
    @ApiResponse(responseCode = "200", description = "Notificaciones con ese estado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoNotificacionResponse.class),
            examples = @ExampleObject(value = "[{\"idNotificacion\": 1, \"estado\": \"PENDIENTE\", \"destinatario\": \"user@example.com\"}]")))
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<DtoNotificacionResponse>> obtenerPorEstado(
            @Parameter(description = "Estado de la notificación", example = "PENDIENTE") @PathVariable EstadoNotificacion estado) {
        return ResponseEntity.ok(notificacionService.obtenerPorEstado(estado));
    }

    @Operation(description = "Crea una nueva notificación en estado PENDIENTE")
    @ApiResponse(responseCode = "201", description = "Notificación creada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoNotificacionResponse.class),
            examples = @ExampleObject(value = "{\"idNotificacion\": 1, \"tipo\": \"EMAIL\", \"destinatario\": \"user@example.com\", \"estado\": \"PENDIENTE\"}")))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"El destinatario es obligatorio\"}")))
    @ApiResponse(responseCode = "401", description = "No autorizado - token JWT requerido")
    @PostMapping
    public ResponseEntity<DtoNotificacionResponse> crear(@Valid @RequestBody DtoNotificacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacionService.crear(request));
    }

    @Operation(description = "Marca una notificación como ENVIADO y registra la fecha de envío")
    @ApiResponse(responseCode = "200", description = "Notificación marcada como enviada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoNotificacionResponse.class),
            examples = @ExampleObject(value = "{\"idNotificacion\": 1, \"estado\": \"ENVIADO\", \"fechaEnvio\": \"2026-06-24T10:00:00\"}")))
    @ApiResponse(responseCode = "401", description = "No autorizado - token JWT requerido")
    @ApiResponse(responseCode = "404", description = "Notificación no encontrada",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"Notificación no encontrada con id: 99\"}")))
    @PatchMapping("/{id}/enviada")
    public ResponseEntity<DtoNotificacionResponse> marcarComoEnviada(
            @Parameter(description = "ID de la notificación", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoEnviada(id));
    }

    @Operation(description = "Marca una notificación como FALLIDO")
    @ApiResponse(responseCode = "200", description = "Notificación marcada como fallida",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoNotificacionResponse.class),
            examples = @ExampleObject(value = "{\"idNotificacion\": 1, \"estado\": \"FALLIDO\"}")))
    @ApiResponse(responseCode = "401", description = "No autorizado - token JWT requerido")
    @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    @PatchMapping("/{id}/fallida")
    public ResponseEntity<DtoNotificacionResponse> marcarComoFallida(
            @Parameter(description = "ID de la notificación", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoFallida(id));
    }

    @Operation(description = "Elimina una notificación por su ID")
    @ApiResponse(responseCode = "204", description = "Notificación eliminada exitosamente")
    @ApiResponse(responseCode = "401", description = "No autorizado - token JWT requerido")
    @ApiResponse(responseCode = "404", description = "Notificación no encontrada",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"Notificación no encontrada con id: 99\"}")))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la notificación a eliminar", example = "1") @PathVariable Long id) {
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
