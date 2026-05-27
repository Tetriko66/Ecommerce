package com.Fullstack.Pago.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Fullstack.Pago.DTO.Request;
import com.Fullstack.Pago.DTO.Response;
import com.Fullstack.Pago.Service.PagoService;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<Response> registrar(@RequestBody Request request) {
        return ResponseEntity.ok(pagoService.registrarPago(request));
    }

    @GetMapping
    public ResponseEntity<List<Response>> listar() {
        return ResponseEntity.ok(pagoService.listarPagos());
    }

    @PatchMapping("/{id}/monto")
    public ResponseEntity<Response> actualizarMonto(@PathVariable Long id, @RequestParam Double monto) {
        return ResponseEntity.ok(pagoService.actualizarMonto(id, monto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pagoService.eliminarPago(id);
        return ResponseEntity.noContent().build();
    }
}