package com.Fullstack.Carro.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Fullstack.Carro.DTO.Request;
import com.Fullstack.Carro.DTO.Response;
import com.Fullstack.Carro.Service.ServiceCarro;

import java.util.List;

@RestController
@RequestMapping("/carrito")
public class ControllerCarro {

    private final ServiceCarro service;

    public ControllerCarro(ServiceCarro service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Response> agregar(@RequestBody Request request) {
        return ResponseEntity.ok(service.agregar(request));
    }

    @GetMapping
    public ResponseEntity<List<Response>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PatchMapping("/{id}/cantidad")
    public ResponseEntity<Response> actualizarCantidad(@PathVariable Long id, @RequestParam Integer cantidad) {
        return ResponseEntity.ok(service.actualizarCantidad(id, cantidad));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
