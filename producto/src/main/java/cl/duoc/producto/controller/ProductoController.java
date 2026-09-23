package cl.duoc.producto.controller;

import cl.duoc.producto.dto.request.DtoProductoRequest;
import cl.duoc.producto.dto.response.DtoProductoResponse;
import cl.duoc.producto.service.ProductoService;
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
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Producto API", description = "Operaciones CRUD para productos del ecommerce")
public class ProductoController {

    private final ProductoService productoService;

    @Operation(description = "Obtiene la lista de todos los productos")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoProductoResponse.class),
        examples = @ExampleObject(value = "[{\"idProducto\": 1, \"nombre\": \"Producto 1\", \"precio\": 9990}]")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @GetMapping
    public ResponseEntity<List<DtoProductoResponse>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @Operation(description = "Obtiene la lista de productos activos")
    @ApiResponse(responseCode = "200", description = "Lista de productos activos obtenida exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoProductoResponse.class),
        examples = @ExampleObject(value = "[{\"idProducto\": 1, \"nombre\": \"Producto 1\", \"activo\": true}]")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @GetMapping("/activos")
    public ResponseEntity<List<DtoProductoResponse>> obtenerActivos() {
        return ResponseEntity.ok(productoService.obtenerActivos());
    }

    @Operation(description = "Obtiene un producto por su ID")
    @ApiResponse(responseCode = "200", description = "Producto encontrado",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoProductoResponse.class),
        examples = @ExampleObject(value = "{\"idProducto\": 1, \"nombre\": \"Producto 1\", \"precio\": 9990}")))
    @ApiResponse(responseCode = "404", description = "Producto no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"Producto no encontrado\"}")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @GetMapping("/{id}")
    public ResponseEntity<DtoProductoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @Operation(description = "Obtiene productos filtrados por categoría")
    @ApiResponse(responseCode = "200", description = "Productos de la categoría obtenidos exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoProductoResponse.class),
        examples = @ExampleObject(value = "[{\"idProducto\": 1, \"nombre\": \"Producto 1\", \"categoria\": \"electronica\"}]")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<DtoProductoResponse>> obtenerPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoria));
    }

    @Operation(description = "Crea un nuevo producto")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoProductoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 400, \"mensaje\": \"Datos de entrada inválidos\"}")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @PostMapping
    public ResponseEntity<DtoProductoResponse> crear(@Valid @RequestBody DtoProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    @Operation(description = "Actualiza un producto existente por su ID")
    @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DtoProductoResponse.class)))
    @ApiResponse(responseCode = "404", description = "Producto no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"Producto no encontrado\"}")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @PutMapping("/{id}")
    public ResponseEntity<DtoProductoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DtoProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @Operation(description = "Desactiva un producto por su ID (baja lógica)")
    @ApiResponse(responseCode = "204", description = "Producto desactivado exitosamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"Producto no encontrado\"}")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        productoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Elimina un producto por su ID")
    @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"Producto no encontrado\"}")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
