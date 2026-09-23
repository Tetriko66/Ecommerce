package cl.duoc.inventario.controller;

import cl.duoc.inventario.dto.request.DtoAjusteStockRequest;
import cl.duoc.inventario.dto.request.DtoInventarioRequest;
import cl.duoc.inventario.dto.response.DtoInventarioResponse;
import cl.duoc.inventario.service.InventarioService;
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
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
@Tag(name = "Inventario API", description = "Operaciones de gestión de inventario y stock")
public class InventarioController {

    private final InventarioService inventarioService;

    @Operation(description = "Obtiene todos los registros de inventario")
    @ApiResponse(responseCode = "200", description = "Lista de registros de inventario obtenida exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoInventarioResponse.class),
        examples = @ExampleObject(value = "[{\"idInventario\": 1, \"productoId\": 1, \"cantidad\": 100}]")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @GetMapping
    public ResponseEntity<List<DtoInventarioResponse>> obtenerTodos() {
        return ResponseEntity.ok(inventarioService.obtenerTodos());
    }

    @Operation(description = "Obtiene un registro de inventario por su ID")
    @ApiResponse(responseCode = "200", description = "Registro de inventario encontrado",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoInventarioResponse.class),
        examples = @ExampleObject(value = "{\"idInventario\": 1, \"productoId\": 1, \"cantidad\": 100}")))
    @ApiResponse(responseCode = "404", description = "Registro de inventario no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"Registro de inventario no encontrado\"}")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @GetMapping("/{id}")
    public ResponseEntity<DtoInventarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerPorId(id));
    }

    @Operation(description = "Obtiene el inventario de un producto específico")
    @ApiResponse(responseCode = "200", description = "Inventario del producto obtenido exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoInventarioResponse.class),
        examples = @ExampleObject(value = "{\"idInventario\": 1, \"productoId\": 5, \"cantidad\": 50, \"stockBajo\": false}")))
    @ApiResponse(responseCode = "404", description = "Inventario del producto no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"Inventario del producto no encontrado\"}")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<DtoInventarioResponse> obtenerPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.obtenerPorProducto(productoId));
    }

    @Operation(description = "Crea un nuevo registro de inventario para un producto")
    @ApiResponse(responseCode = "201", description = "Registro de inventario creado exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoInventarioResponse.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 400, \"mensaje\": \"Datos de entrada inválidos\"}")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @PostMapping
    public ResponseEntity<DtoInventarioResponse> crear(@Valid @RequestBody DtoInventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crear(request));
    }

    @Operation(description = "Agrega stock al inventario de un producto")
    @ApiResponse(responseCode = "200", description = "Stock agregado exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoInventarioResponse.class),
        examples = @ExampleObject(value = "{\"idInventario\": 1, \"productoId\": 1, \"cantidad\": 150}")))
    @ApiResponse(responseCode = "404", description = "Inventario del producto no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"Inventario del producto no encontrado\"}")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @PatchMapping("/producto/{productoId}/agregar")
    public ResponseEntity<DtoInventarioResponse> agregarStock(
            @PathVariable Long productoId,
            @Valid @RequestBody DtoAjusteStockRequest request) {
        return ResponseEntity.ok(inventarioService.agregarStock(productoId, request));
    }

    @Operation(description = "Reduce stock del inventario de un producto")
    @ApiResponse(responseCode = "200", description = "Stock reducido exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoInventarioResponse.class),
        examples = @ExampleObject(value = "{\"idInventario\": 1, \"productoId\": 1, \"cantidad\": 80}")))
    @ApiResponse(responseCode = "404", description = "Inventario del producto no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"Inventario del producto no encontrado\"}")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @PatchMapping("/producto/{productoId}/reducir")
    public ResponseEntity<DtoInventarioResponse> reducirStock(
            @PathVariable Long productoId,
            @Valid @RequestBody DtoAjusteStockRequest request) {
        return ResponseEntity.ok(inventarioService.reducirStock(productoId, request));
    }

    @Operation(description = "Elimina un registro de inventario por su ID")
    @ApiResponse(responseCode = "204", description = "Registro de inventario eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Registro de inventario no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"Registro de inventario no encontrado\"}")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
