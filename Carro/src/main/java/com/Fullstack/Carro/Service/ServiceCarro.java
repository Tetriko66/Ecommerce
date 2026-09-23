package com.Fullstack.Carro.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.Fullstack.Carro.DTO.Request;
import com.Fullstack.Carro.DTO.Response;
import com.Fullstack.Carro.Model.ModelCarro;
import com.Fullstack.Carro.Repository.RepositoryCarro;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ServiceCarro {

    private static final Logger log = LoggerFactory.getLogger(ServiceCarro.class);

    private final RepositoryCarro repository;
    private final WebClient.Builder webClientBuilder;

    @Value("${producto.service.url}")
    private String productoServiceUrl;

    @Value("${inventario.service.url}")
    private String inventarioServiceUrl;

    public ServiceCarro(RepositoryCarro repository, WebClient.Builder webClientBuilder) {
        this.repository = repository;
        this.webClientBuilder = webClientBuilder;
    }

    public Response agregar(Request request) {
        log.info("Agregando producto id: {} al carrito, cantidad: {}", request.getProductoId(), request.getCantidad());

        validarProductoExiste(request.getProductoId());
        validarStockDisponible(request.getProductoId(), request.getCantidad());

        ModelCarro model = new ModelCarro();
        model.setUsuarioId(request.getUsuarioId());
        model.setProductoId(request.getProductoId());
        model.setCantidad(request.getCantidad());

        repository.save(model);
        log.info("Producto id: {} agregado al carrito con id: {}", request.getProductoId(), model.getId());

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

        log.info("Actualizando cantidad del carrito id: {} a {}", id, cantidad);
        validarStockDisponible(model.getProductoId(), cantidad);

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
        log.info("Item id: {} eliminado del carrito", id);
    }

    private void validarProductoExiste(Long productoId) {
        try {
            log.info("Validando existencia del producto id: {} en product-service", productoId);
            webClientBuilder.build()
                    .get()
                    .uri(productoServiceUrl + "/api/v1/productos/" + productoId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            log.info("Producto id: {} validado correctamente", productoId);
        } catch (WebClientResponseException.NotFound e) {
            log.error("Producto id: {} no existe en product-service", productoId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "El producto con id " + productoId + " no existe");
        } catch (Exception e) {
            log.warn("No se pudo contactar product-service: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con el servicio de productos");
        }
    }

    private void validarStockDisponible(Long productoId, Integer cantidadSolicitada) {
        try {
            log.info("Verificando stock del producto id: {} para cantidad: {}", productoId, cantidadSolicitada);

            java.util.Map<?, ?> inventario = webClientBuilder.build()
                    .get()
                    .uri(inventarioServiceUrl + "/api/v1/inventario/producto/" + productoId)
                    .retrieve()
                    .bodyToMono(java.util.Map.class)
                    .block();

            if (inventario == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No hay inventario registrado para el producto con id " + productoId);
            }

            Integer stockDisponible = (Integer) inventario.get("cantidad");
            if (stockDisponible == null || stockDisponible < cantidadSolicitada) {
                log.error("Stock insuficiente para producto id: {}. Disponible: {}, Solicitado: {}",
                        productoId, stockDisponible, cantidadSolicitada);
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stock insuficiente. Disponible: " + stockDisponible + ", Solicitado: " + cantidadSolicitada);
            }

            log.info("Stock verificado para producto id: {}. Disponible: {}", productoId, stockDisponible);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (WebClientResponseException.NotFound e) {
            log.error("No existe inventario para el producto id: {}", productoId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No hay inventario registrado para el producto con id " + productoId);
        } catch (Exception e) {
            log.warn("No se pudo contactar inventario-service: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con el servicio de inventario");
        }
    }
}
