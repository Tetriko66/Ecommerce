package com.Fullstack.Pago.Controller;

import com.Fullstack.Pago.DTO.Request;
import com.Fullstack.Pago.DTO.Response;
import com.Fullstack.Pago.Service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1/pagos")
@Tag(name = "Pago API", description = "Operaciones para registrar y consultar pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @Operation(description = "Registra un nuevo pago asociado a un pedido")
    @ApiResponse(responseCode = "200", description = "Pago registrado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class),
            examples = @ExampleObject(value = "{\"id\": 1, \"usuarioId\": 1, \"pedidoId\": 10, \"monto\": 5000.0}")))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"El monto es obligatorio\"}")))
    @ApiResponse(responseCode = "401", description = "No autorizado - token JWT requerido")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"Error interno del servidor\"}")))
    @PostMapping
    public ResponseEntity<Response> registrar(@Valid @RequestBody Request request) {
        return ResponseEntity.ok(pagoService.registrarPago(request));
    }

    @Operation(description = "Obtiene la lista de todos los pagos registrados")
    @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class),
            examples = @ExampleObject(value = "[{\"id\": 1, \"usuarioId\": 1, \"pedidoId\": 10, \"monto\": 5000.0}]")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"Error interno del servidor\"}")))
    @GetMapping
    public ResponseEntity<List<Response>> listar() {
        return ResponseEntity.ok(pagoService.listarPagos());
    }

    @Operation(description = "Actualiza el monto de un pago existente")
    @ApiResponse(responseCode = "200", description = "Monto actualizado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class),
            examples = @ExampleObject(value = "{\"id\": 1, \"usuarioId\": 1, \"pedidoId\": 10, \"monto\": 7500.0}")))
    @ApiResponse(responseCode = "401", description = "No autorizado - token JWT requerido")
    @ApiResponse(responseCode = "404", description = "Pago no encontrado",
            content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\"error\": \"Pago no encontrado\"}")))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @PatchMapping("/{id}/monto")
    public ResponseEntity<Response> actualizarMonto(
            @Parameter(description = "ID del pago", example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo monto del pago", example = "7500.0") @RequestParam Double monto) {
        return ResponseEntity.ok(pagoService.actualizarMonto(id, monto));
    }

    @Operation(description = "Elimina un pago por su ID")
    @ApiResponse(responseCode = "204", description = "Pago eliminado exitosamente")
    @ApiResponse(responseCode = "401", description = "No autorizado - token JWT requerido")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del pago a eliminar", example = "1") @PathVariable Long id) {
        pagoService.eliminarPago(id);
        return ResponseEntity.noContent().build();
    }
}
