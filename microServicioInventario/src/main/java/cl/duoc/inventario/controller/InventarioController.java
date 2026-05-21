package cl.duoc.inventario.controller;

import cl.duoc.inventario.dto.request.DtoAjusteStockRequest;
import cl.duoc.inventario.dto.request.DtoInventarioRequest;
import cl.duoc.inventario.dto.response.DtoInventarioResponse;
import cl.duoc.inventario.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    // GET /api/v1/inventario — Todos los registros
    @GetMapping
    public ResponseEntity<List<DtoInventarioResponse>> obtenerTodos() {
        return ResponseEntity.ok(inventarioService.obtenerTodos());
    }

    // GET /api/v1/inventario/{id} — Registro por ID
    @GetMapping("/{id}")
    public ResponseEntity<DtoInventarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerPorId(id));
    }

    // GET /api/v1/inventario/producto/{productoId} — Inventario de un producto
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<DtoInventarioResponse> obtenerPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.obtenerPorProducto(productoId));
    }

    // POST /api/v1/inventario — Crear registro de inventario
    @PostMapping
    public ResponseEntity<DtoInventarioResponse> crear(@Valid @RequestBody DtoInventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crear(request));
    }

    // PATCH /api/v1/inventario/producto/{productoId}/agregar — Agregar stock
    @PatchMapping("/producto/{productoId}/agregar")
    public ResponseEntity<DtoInventarioResponse> agregarStock(
            @PathVariable Long productoId,
            @Valid @RequestBody DtoAjusteStockRequest request) {
        return ResponseEntity.ok(inventarioService.agregarStock(productoId, request));
    }

    // PATCH /api/v1/inventario/producto/{productoId}/reducir — Reducir stock
    @PatchMapping("/producto/{productoId}/reducir")
    public ResponseEntity<DtoInventarioResponse> reducirStock(
            @PathVariable Long productoId,
            @Valid @RequestBody DtoAjusteStockRequest request) {
        return ResponseEntity.ok(inventarioService.reducirStock(productoId, request));
    }

    // DELETE /api/v1/inventario/{id} — Eliminar registro
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
