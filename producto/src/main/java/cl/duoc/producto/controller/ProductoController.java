package cl.duoc.producto.controller;

import cl.duoc.producto.dto.request.DtoProductoRequest;
import cl.duoc.producto.dto.response.DtoProductoResponse;
import cl.duoc.producto.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // GET /api/v1/productos — Todos los productos
    @GetMapping
    public ResponseEntity<List<DtoProductoResponse>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    // GET /api/v1/productos/activos — Solo productos activos
    @GetMapping("/activos")
    public ResponseEntity<List<DtoProductoResponse>> obtenerActivos() {
        return ResponseEntity.ok(productoService.obtenerActivos());
    }

    // GET /api/v1/productos/{id} — Producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<DtoProductoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    // GET /api/v1/productos/categoria/{categoria} — Productos por categoría
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<DtoProductoResponse>> obtenerPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoria));
    }

    // POST /api/v1/productos — Crear producto
    @PostMapping
    public ResponseEntity<DtoProductoResponse> crear(@Valid @RequestBody DtoProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    // PUT /api/v1/productos/{id} — Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<DtoProductoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DtoProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    // PATCH /api/v1/productos/{id}/desactivar — Desactivar producto
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        productoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/productos/{id} — Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
