package cl.duoc.MicroServicioNotificaciones.controller;

import cl.duoc.MicroServicioNotificaciones.dto.request.DtoNotificacionRequest;
import cl.duoc.MicroServicioNotificaciones.dto.response.DtoNotificacionResponse;
import cl.duoc.MicroServicioNotificaciones.model.EstadoNotificacion;
import cl.duoc.MicroServicioNotificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Indica que esta clase es un controlador REST
@RestController

// Define la ruta base del controlador
@RequestMapping("/api/v1/notificaciones")

// Genera constructor con atributos final
@RequiredArgsConstructor
public class NotificacionController {

    // Inyecta el servicio de notificaciones
    private final NotificacionService notificacionService;

    // GET /api/v1/notificaciones — Todas las notificaciones
    @GetMapping
    public ResponseEntity<List<DtoNotificacionResponse>> obtenerTodas() {
        return ResponseEntity.ok(notificacionService.obtenerTodas());
    }

    // GET /api/v1/notificaciones/{id} — Notificación por ID
    @GetMapping("/{id}")
    public ResponseEntity<DtoNotificacionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.obtenerPorId(id));
    }

    // GET /api/v1/notificaciones/estado/{estado} — Notificaciones por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<DtoNotificacionResponse>> obtenerPorEstado(
            @PathVariable EstadoNotificacion estado) {
        return ResponseEntity.ok(notificacionService.obtenerPorEstado(estado));
    }

    // POST /api/v1/notificaciones — Crear nueva notificación
    @PostMapping
    public ResponseEntity<DtoNotificacionResponse> crear(
            @Valid @RequestBody DtoNotificacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacionService.crear(request));
    }

    // PATCH /api/v1/notificaciones/{id}/enviada — Marcar como enviada
    @PatchMapping("/{id}/enviada")
    public ResponseEntity<DtoNotificacionResponse> marcarComoEnviada(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoEnviada(id));
    }

    // PATCH /api/v1/notificaciones/{id}/fallida — Marcar como fallida
    @PatchMapping("/{id}/fallida")
    public ResponseEntity<DtoNotificacionResponse> marcarComoFallida(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoFallida(id));
    }

    // DELETE /api/v1/notificaciones/{id} — Eliminar notificación
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
