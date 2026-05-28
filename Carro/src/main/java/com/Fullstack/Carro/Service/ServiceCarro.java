package com.Fullstack.Carro.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.Fullstack.Carro.DTO.Request;
import com.Fullstack.Carro.DTO.Response;
import com.Fullstack.Carro.Model.ModelCarro;
import com.Fullstack.Carro.Repository.RepositoryCarro;

import jakarta.persistence.EntityNotFoundException;


@Service
public class ServiceCarro {

    private final RepositoryCarro repository;

    public ServiceCarro(RepositoryCarro repository) {
        this.repository = repository;
    }

    public Response agregar(Request request) {
        ModelCarro model = new ModelCarro();
        model.setUsuarioId(request.getUsuarioId());
        model.setProductoId(request.getProductoId());
        model.setCantidad(request.getCantidad());

        repository.save(model);

        return new Response(
                model.getId(),
                model.getUsuarioId(),
                model.getProductoId(),
                model.getCantidad(),
                model.getFechaCreacion()
        );
    }

    public List<Response> listar() {
        return repository.findAll().stream()
                .map(m -> new Response(
                        m.getId(),
                        m.getUsuarioId(),
                        m.getProductoId(),
                        m.getCantidad(),
                        m.getFechaCreacion()
                ))
                .collect(Collectors.toList());
    }

    public Response actualizarCantidad(Long id, Integer cantidad) {
        ModelCarro model = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Carrito no encontrado con id " + id));

        model.setCantidad(cantidad);
        repository.save(model);

        return new Response(
                model.getId(),
                model.getUsuarioId(),
                model.getProductoId(),
                model.getCantidad(),
                model.getFechaCreacion()
        );
    }

    public void eliminar(Long id) {
        ModelCarro model = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Carrito no encontrado con id " + id));
        repository.delete(model);
    }
}
