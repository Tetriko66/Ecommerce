package cl.duoc.despacho.controller;

import cl.duoc.despacho.dto.request.DtoDespachoRequest;
import cl.duoc.despacho.dto.response.DtoDespachoResponse;
import cl.duoc.despacho.service.DespachoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@RestController
@RequestMapping("/api/v1/despachos")
@RequiredArgsConstructor
public class DespachoController {

    private final DespachoService despachoService;

    // GET /api/v1/despachos
    @GetMapping
    public ResponseEntity<List<DtoDespachoResponse>> obtenerTodos() {
        return ResponseEntity.ok(despachoService.obtenerTodos());
    }

    // GET /api/v1/despachos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DtoDespachoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(despachoService.obtenerPorId(id));
    }

    // GET /api/v1/despachos/pedido/{pedidoId}
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<DtoDespachoResponse> obtenerPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(despachoService.obtenerPorPedido(pedidoId));
    }

    // GET /api/v1/despachos/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<DtoDespachoResponse>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(despachoService.obtenerPorEstado(estado));
    }

    // POST /api/v1/despachos
    @PostMapping
    public ResponseEntity<DtoDespachoResponse> crear(
            @Valid @RequestBody DtoDespachoRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.status(HttpStatus.CREATED).body(despachoService.crear(request, authHeader));
    }

    // PATCH /api/v1/despachos/{id}/estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<DtoDespachoResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return ResponseEntity.ok(despachoService.actualizarEstado(id, estado));
    }

    // DELETE /api/v1/despachos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        despachoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
