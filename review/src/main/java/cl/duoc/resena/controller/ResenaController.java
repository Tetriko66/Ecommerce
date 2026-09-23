package cl.duoc.resena.controller;

import cl.duoc.resena.dto.request.DtoResenaRequest;
import cl.duoc.resena.dto.response.DtoResenaResponse;
import cl.duoc.resena.service.ResenaService;
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
public class ResenaController {

    private final ResenaService resenaService;

    @GetMapping
    public ResponseEntity<List<DtoResenaResponse>> obtenerTodos() {
        return ResponseEntity.ok(resenaService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DtoResenaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<DtoResenaResponse>> obtenerPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(resenaService.obtenerPorProducto(productoId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DtoResenaResponse>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(resenaService.obtenerPorUsuario(usuarioId));
    }

    // GET /api/v1/resenas/producto/{productoId}/promedio — Promedio de calificaciones
    @GetMapping("/producto/{productoId}/promedio")
    public ResponseEntity<Map<String, Object>> obtenerPromedio(@PathVariable Long productoId) {
        Double promedio = resenaService.obtenerPromedioProducto(productoId);
        return ResponseEntity.ok(Map.of("productoId", productoId, "promedio", promedio));
    }

    @PostMapping
    public ResponseEntity<DtoResenaResponse> crear(@Valid @RequestBody DtoResenaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DtoResenaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DtoResenaRequest request) {
        return ResponseEntity.ok(resenaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
