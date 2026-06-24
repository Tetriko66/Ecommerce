package com.Fullstack.Carro.Controller;

import com.Fullstack.Carro.DTO.Request;
import com.Fullstack.Carro.DTO.Response;
import com.Fullstack.Carro.Service.ServiceCarro;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carrito")
@Tag(name = "Carrito API", description = "Operaciones CRUD para el carrito de compras")
public class ControllerCarro {

    private final ServiceCarro service;

    public ControllerCarro(ServiceCarro service) {
        this.service = service;
    }

    @Operation(description = "Agrega un producto al carrito")
    @ApiResponse(responseCode = "200", description = "Producto agregado exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    @ApiResponse(responseCode = "404", description = "Producto o inventario no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"El producto no existe\"}")))
    @ApiResponse(responseCode = "409", description = "Stock insuficiente",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 409, \"mensaje\": \"Stock insuficiente\"}")))
    @ApiResponse(responseCode = "503", description = "Servicio no disponible",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 503, \"mensaje\": \"No se pudo conectar con el servicio\"}")))
    @PostMapping
    public ResponseEntity<Response> agregar(@Valid @RequestBody Request request) {
        return ResponseEntity.ok(service.agregar(request));
    }

    @Operation(description = "Obtiene todos los items del carrito")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class),
        examples = @ExampleObject(value = "[{\"id\": 1, \"usuarioId\": 1, \"productoId\": 10, \"cantidad\": 2}]")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @GetMapping
    public ResponseEntity<List<Response>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(description = "Actualiza la cantidad de un item del carrito")
    @ApiResponse(responseCode = "200", description = "Cantidad actualizada exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    @ApiResponse(responseCode = "404", description = "Item no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"Carrito no encontrado con id 1\"}")))
    @ApiResponse(responseCode = "409", description = "Stock insuficiente",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 409, \"mensaje\": \"Stock insuficiente\"}")))
    @PatchMapping("/{id}/cantidad")
    public ResponseEntity<Response> actualizarCantidad(@PathVariable Long id, @RequestParam Integer cantidad) {
        return ResponseEntity.ok(service.actualizarCantidad(id, cantidad));
    }

    @Operation(description = "Elimina un item del carrito por su ID")
    @ApiResponse(responseCode = "204", description = "Item eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Item no encontrado",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 404, \"mensaje\": \"Carrito no encontrado con id 1\"}")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json",
        examples = @ExampleObject(value = "{\"status\": 500, \"mensaje\": \"Error interno del servidor\"}")))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}