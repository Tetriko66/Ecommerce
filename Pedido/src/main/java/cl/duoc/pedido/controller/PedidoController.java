package cl.duoc.pedido.controller;

import cl.duoc.pedido.dto.request.DtoPedidoRequest;
import cl.duoc.pedido.dto.response.DtoPedidoResponse;
import cl.duoc.pedido.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedido API", description = "Operaciones CRUD para pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(description = "Obtiene todos los pedidos")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoPedidoResponse.class),
        examples = @ExampleObject(value = "[{\"idPedido\": 1, \"usuarioId\": 1, \"productoId\": 10, \"cantidad\": 2, \"montoTotal\": 5000.0, \"estado\": \"PAGADO\"}]")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @GetMapping
    public ResponseEntity<List<DtoPedidoResponse>> obtenerTodos() {
        return ResponseEntity.ok(pedidoService.obtenerTodos());
    }

    @Operation(description = "Obtiene un pedido por su ID")
    @ApiResponse(responseCode = "200", description = "Pedido encontrado",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoPedidoResponse.class)))
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"Pedido no encontrado con id: 1\"}")))
    @GetMapping("/{id}")
    public ResponseEntity<DtoPedidoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    @Operation(description = "Obtiene pedidos por ID de usuario")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos del usuario",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoPedidoResponse.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DtoPedidoResponse>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(pedidoService.obtenerPorUsuario(usuarioId));
    }

    @Operation(description = "Obtiene pedidos por estado")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos con ese estado",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoPedidoResponse.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<DtoPedidoResponse>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pedidoService.obtenerPorEstado(estado));
    }

    @Operation(description = "Crea un nuevo pedido")
    @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoPedidoResponse.class)))
    @ApiResponse(responseCode = "404", description = "Producto o inventario no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"No hay inventario registrado para el producto\"}")))
    @ApiResponse(responseCode = "409", description = "Stock insuficiente",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 409, \"mensaje\": \"Stock insuficiente para el producto\"}")))
    @ApiResponse(responseCode = "503", description = "Servicio no disponible",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 503, \"mensaje\": \"No se pudo conectar con el servicio de inventario\"}")))
    @PostMapping
    public ResponseEntity<DtoPedidoResponse> crear(
            @Valid @RequestBody DtoPedidoRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(request, authHeader));
    }

    @Operation(description = "Actualiza el estado de un pedido")
    @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoPedidoResponse.class)))
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"Pedido no encontrado con id: 1\"}")))
    @PatchMapping("/{id}/estado")
    public ResponseEntity<DtoPedidoResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, estado));
    }

    @Operation(description = "Elimina un pedido por su ID")
    @ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"Pedido no encontrado con id: 1\"}")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}