package cl.duoc.pedido.controller;

import cl.duoc.pedido.dto.request.DtoPedidoRequest;
import cl.duoc.pedido.dto.response.DtoPedidoResponse;
import cl.duoc.pedido.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    // GET /api/v1/pedidos
    @GetMapping
    public ResponseEntity<List<DtoPedidoResponse>> obtenerTodos() {
        return ResponseEntity.ok(pedidoService.obtenerTodos());
    }

    // GET /api/v1/pedidos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DtoPedidoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    // GET /api/v1/pedidos/usuario/{usuarioId}
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DtoPedidoResponse>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(pedidoService.obtenerPorUsuario(usuarioId));
    }

    // GET /api/v1/pedidos/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<DtoPedidoResponse>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pedidoService.obtenerPorEstado(estado));
    }

    // POST /api/v1/pedidos
    @PostMapping
    public ResponseEntity<DtoPedidoResponse> crear(
            @Valid @RequestBody DtoPedidoRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(request, authHeader));
    }

    // PATCH /api/v1/pedidos/{id}/estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<DtoPedidoResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, estado));
    }

    // DELETE /api/v1/pedidos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
