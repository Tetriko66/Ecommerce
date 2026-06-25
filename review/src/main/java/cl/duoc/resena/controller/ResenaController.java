package cl.duoc.resena.controller;

import cl.duoc.resena.dto.request.DtoResenaRequest;
import cl.duoc.resena.dto.response.DtoResenaResponse;
import cl.duoc.resena.service.ResenaService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resenas")
@RequiredArgsConstructor
@Tag(name = "Reseña API", description = "Operaciones para reseñas y calificaciones de productos")
public class ResenaController {

    private final ResenaService resenaService;

    @Operation(description = "Obtiene todas las reseñas registradas")
    @ApiResponse(responseCode = "200", description = "Lista de reseñas obtenida exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoResenaResponse.class),
            examples = @ExampleObject(value = "[{\"idResena\": 1, \"usuarioId\": 1, \"productoId\": 10, \"calificacion\": 5, \"comentario\": \"Excelente producto\"}]")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping
    public ResponseEntity<List<DtoResenaResponse>> obtenerTodos() {
        return ResponseEntity.ok(resenaService.obtenerTodos());
    }

    @Operation(description = "Obtiene una reseña por su ID")
    @ApiResponse(responseCode = "200", description = "Reseña encontrada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoResenaResponse.class),
            examples = @ExampleObject(value = "{\"idResena\": 1, \"usuarioId\": 1, \"productoId\": 10, \"calificacion\": 5, \"comentario\": \"Excelente producto\"}")))
    @ApiResponse(responseCode = "404", description = "Reseña no encontrada",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"Reseña no encontrada con id: 99\"}")))
    @GetMapping("/{id}")
    public ResponseEntity<DtoResenaResponse> obtenerPorId(
            @Parameter(description = "ID de la reseña", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    @Operation(description = "Obtiene todas las reseñas de un producto")
    @ApiResponse(responseCode = "200", description = "Reseñas del producto obtenidas exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoResenaResponse.class),
            examples = @ExampleObject(value = "[{\"idResena\": 1, \"productoId\": 10, \"calificacion\": 4}]")))
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<DtoResenaResponse>> obtenerPorProducto(
            @Parameter(description = "ID del producto", example = "10") @PathVariable Long productoId) {
        return ResponseEntity.ok(resenaService.obtenerPorProducto(productoId));
    }

    @Operation(description = "Obtiene todas las reseñas de un usuario")
    @ApiResponse(responseCode = "200", description = "Reseñas del usuario obtenidas exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoResenaResponse.class)))
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DtoResenaResponse>> obtenerPorUsuario(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(resenaService.obtenerPorUsuario(usuarioId));
    }

    @Operation(description = "Calcula el promedio de calificaciones de un producto")
    @ApiResponse(responseCode = "200", description = "Promedio calculado exitosamente",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"productoId\": 10, \"promedio\": 4.5}")))
    @GetMapping("/producto/{productoId}/promedio")
    public ResponseEntity<Map<String, Object>> obtenerPromedio(
            @Parameter(description = "ID del producto", example = "10") @PathVariable Long productoId) {
        Double promedio = resenaService.obtenerPromedioProducto(productoId);
        return ResponseEntity.ok(Map.of("productoId", productoId, "promedio", promedio));
    }

    @Operation(description = "Crea una nueva reseña para un producto")
    @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoResenaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"La calificación mínima es 1\"}")))
    @ApiResponse(responseCode = "401", description = "No autorizado - token JWT requerido")
    @ApiResponse(responseCode = "404", description = "Usuario o producto no encontrado",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"El producto con id 10 no existe\"}")))
    @ApiResponse(responseCode = "409", description = "El usuario ya reseñó este producto",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"El usuario ya tiene una reseña para este producto\"}")))
    @ApiResponse(responseCode = "503", description = "Servicio externo no disponible")
    @PostMapping
    public ResponseEntity<DtoResenaResponse> crear(@Valid @RequestBody DtoResenaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.crear(request));
    }

    @Operation(description = "Actualiza una reseña existente")
    @ApiResponse(responseCode = "200", description = "Reseña actualizada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoResenaResponse.class)))
    @ApiResponse(responseCode = "401", description = "No autorizado - token JWT requerido")
    @ApiResponse(responseCode = "404", description = "Reseña no encontrada",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"Reseña no encontrada con id: 99\"}")))
    @PutMapping("/{id}")
    public ResponseEntity<DtoResenaResponse> actualizar(
            @Parameter(description = "ID de la reseña", example = "1") @PathVariable Long id,
            @Valid @RequestBody DtoResenaRequest request) {
        return ResponseEntity.ok(resenaService.actualizar(id, request));
    }

    @Operation(description = "Elimina una reseña por su ID")
    @ApiResponse(responseCode = "204", description = "Reseña eliminada exitosamente")
    @ApiResponse(responseCode = "401", description = "No autorizado - token JWT requerido")
    @ApiResponse(responseCode = "404", description = "Reseña no encontrada",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"Reseña no encontrada con id: 99\"}")))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la reseña a eliminar", example = "1") @PathVariable Long id) {
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
