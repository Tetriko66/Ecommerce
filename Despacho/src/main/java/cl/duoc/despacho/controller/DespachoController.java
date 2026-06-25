package cl.duoc.despacho.controller;

import cl.duoc.despacho.dto.request.DtoDespachoRequest;
import cl.duoc.despacho.dto.response.DtoDespachoResponse;
import cl.duoc.despacho.service.DespachoService;
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
@RequestMapping("/api/v1/despachos")
@RequiredArgsConstructor
@Tag(name = "Despacho API", description = "Operaciones de gestión de despachos y entregas")
public class DespachoController {

    private final DespachoService despachoService;

    @Operation(description = "Obtiene todos los despachos registrados")
    @ApiResponse(responseCode = "200", description = "Lista de despachos obtenida exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoDespachoResponse.class),
            examples = @ExampleObject(value = "[{\"idDespacho\": 1, \"pedidoId\": 10, \"estado\": \"PREPARANDO\", \"direccionEntrega\": \"Av. Principal 123\"}]")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"Error interno del servidor\"}")))
    @GetMapping
    public ResponseEntity<List<DtoDespachoResponse>> obtenerTodos() {
        return ResponseEntity.ok(despachoService.obtenerTodos());
    }

    @Operation(description = "Obtiene un despacho por su ID")
    @ApiResponse(responseCode = "200", description = "Despacho encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoDespachoResponse.class),
            examples = @ExampleObject(value = "{\"idDespacho\": 1, \"pedidoId\": 10, \"estado\": \"PREPARANDO\", \"direccionEntrega\": \"Av. Principal 123\"}")))
    @ApiResponse(responseCode = "404", description = "Despacho no encontrado",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"Despacho no encontrado con id: 99\"}")))
    @GetMapping("/{id}")
    public ResponseEntity<DtoDespachoResponse> obtenerPorId(
            @Parameter(description = "ID del despacho", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(despachoService.obtenerPorId(id));
    }

    @Operation(description = "Obtiene el despacho asociado a un pedido")
    @ApiResponse(responseCode = "200", description = "Despacho del pedido encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoDespachoResponse.class)))
    @ApiResponse(responseCode = "404", description = "No existe despacho para el pedido",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"No existe despacho para el pedido con id: 10\"}")))
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<DtoDespachoResponse> obtenerPorPedido(
            @Parameter(description = "ID del pedido", example = "10") @PathVariable Long pedidoId) {
        return ResponseEntity.ok(despachoService.obtenerPorPedido(pedidoId));
    }

    @Operation(description = "Obtiene despachos filtrados por estado (PREPARANDO, EN_CAMINO, ENTREGADO)")
    @ApiResponse(responseCode = "200", description = "Lista de despachos con ese estado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoDespachoResponse.class),
            examples = @ExampleObject(value = "[{\"idDespacho\": 1, \"pedidoId\": 10, \"estado\": \"EN_CAMINO\"}]")))
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<DtoDespachoResponse>> obtenerPorEstado(
            @Parameter(description = "Estado del despacho", example = "PREPARANDO") @PathVariable String estado) {
        return ResponseEntity.ok(despachoService.obtenerPorEstado(estado));
    }

    @Operation(description = "Crea un nuevo despacho para un pedido existente")
    @ApiResponse(responseCode = "201", description = "Despacho creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoDespachoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"La dirección de entrega es obligatoria\"}")))
    @ApiResponse(responseCode = "401", description = "No autorizado - token JWT requerido")
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"El pedido con id 10 no existe\"}")))
    @ApiResponse(responseCode = "409", description = "Ya existe un despacho para ese pedido",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"Ya existe un despacho para el pedido con id: 10\"}")))
    @ApiResponse(responseCode = "503", description = "Servicio de pedidos no disponible",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"No se pudo conectar con el servicio de pedidos\"}")))
    @PostMapping
    public ResponseEntity<DtoDespachoResponse> crear(
            @Valid @RequestBody DtoDespachoRequest request,
            @Parameter(description = "Token JWT (Bearer)", example = "eyJhbGciOiJIUzI1NiIs...")
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.status(HttpStatus.CREATED).body(despachoService.crear(request, authHeader));
    }

    @Operation(description = "Actualiza el estado de un despacho")
    @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoDespachoResponse.class),
            examples = @ExampleObject(value = "{\"idDespacho\": 1, \"pedidoId\": 10, \"estado\": \"ENTREGADO\"}")))
    @ApiResponse(responseCode = "401", description = "No autorizado - token JWT requerido")
    @ApiResponse(responseCode = "404", description = "Despacho no encontrado",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"Despacho no encontrado con id: 99\"}")))
    @PatchMapping("/{id}/estado")
    public ResponseEntity<DtoDespachoResponse> actualizarEstado(
            @Parameter(description = "ID del despacho", example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo estado", example = "ENTREGADO") @RequestParam String estado) {
        return ResponseEntity.ok(despachoService.actualizarEstado(id, estado));
    }

    @Operation(description = "Elimina un despacho por su ID")
    @ApiResponse(responseCode = "204", description = "Despacho eliminado exitosamente")
    @ApiResponse(responseCode = "401", description = "No autorizado - token JWT requerido")
    @ApiResponse(responseCode = "404", description = "Despacho no encontrado",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"Despacho no encontrado con id: 99\"}")))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del despacho a eliminar", example = "1") @PathVariable Long id) {
        despachoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
