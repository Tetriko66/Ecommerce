package cl.duoc.inventario.service;

import cl.duoc.inventario.dto.request.DtoAjusteStockRequest;
import cl.duoc.inventario.dto.request.DtoInventarioRequest;
import cl.duoc.inventario.dto.response.DtoInventarioResponse;
import cl.duoc.inventario.model.InventarioModel;
import cl.duoc.inventario.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private static final Logger log = LoggerFactory.getLogger(InventarioService.class);

    private final InventarioRepository inventarioRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${producto.service.url}")
    private String productoServiceUrl;

    public List<DtoInventarioResponse> obtenerTodos() {
        log.info("Obteniendo todos los registros de inventario");
        List<InventarioModel> lista = inventarioRepository.findAll();
        log.info("Se encontraron {} registros de inventario", lista.size());
        return lista.stream().map(this::mapearAResponse).collect(Collectors.toList());
    }

    public DtoInventarioResponse obtenerPorId(Long id) {
        log.info("Buscando inventario con id: {}", id);
        InventarioModel inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Inventario con id {} no encontrado", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventario no encontrado con id: " + id);
                });
        return mapearAResponse(inventario);
    }

    public DtoInventarioResponse obtenerPorProducto(Long productoId) {
        log.info("Buscando inventario para producto id: {}", productoId);
        InventarioModel inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> {
                    log.error("No existe inventario para el producto con id: {}", productoId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe inventario para el producto con id: " + productoId);
                });
        return mapearAResponse(inventario);
    }

    public DtoInventarioResponse crear(DtoInventarioRequest request) {
        log.info("Creando inventario para producto id: {}", request.getProductoId());

        // Valida que el producto exista en product-service
        validarProductoExiste(request.getProductoId());

        if (inventarioRepository.existsByProductoId(request.getProductoId())) {
            log.error("Ya existe un registro de inventario para el producto id: {}", request.getProductoId());
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un inventario para el producto con id: " + request.getProductoId());
        }

        InventarioModel inventario = new InventarioModel();
        inventario.setProductoId(request.getProductoId());
        inventario.setCantidad(request.getCantidad());
        inventario.setStockMinimo(request.getStockMinimo());
        inventario.setFechaActualizacion(LocalDateTime.now());

        InventarioModel guardado = inventarioRepository.save(inventario);
        log.info("Inventario creado con id: {} para producto: {}", guardado.getIdInventario(), guardado.getProductoId());
        return mapearAResponse(guardado);
    }

    // Suma stock al inventario de un producto
    public DtoInventarioResponse agregarStock(Long productoId, DtoAjusteStockRequest request) {
        log.info("Agregando {} unidades al producto id: {}", request.getCantidad(), productoId);
        InventarioModel inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe inventario para el producto con id: " + productoId));

        inventario.setCantidad(inventario.getCantidad() + request.getCantidad());
        inventario.setFechaActualizacion(LocalDateTime.now());
        InventarioModel actualizado = inventarioRepository.save(inventario);
        log.info("Stock actualizado a {} unidades para producto id: {}", actualizado.getCantidad(), productoId);
        return mapearAResponse(actualizado);
    }

    // Reduce stock del inventario. Lanza error si no hay suficiente stock
    public DtoInventarioResponse reducirStock(Long productoId, DtoAjusteStockRequest request) {
        log.info("Reduciendo {} unidades del producto id: {}", request.getCantidad(), productoId);
        InventarioModel inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe inventario para el producto con id: " + productoId));

        if (inventario.getCantidad() < request.getCantidad()) {
            log.error("Stock insuficiente para producto id: {}. Disponible: {}, Solicitado: {}",
                    productoId, inventario.getCantidad(), request.getCantidad());
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Stock insuficiente. Disponible: " + inventario.getCantidad() + ", Solicitado: " + request.getCantidad());
        }

        inventario.setCantidad(inventario.getCantidad() - request.getCantidad());
        inventario.setFechaActualizacion(LocalDateTime.now());
        InventarioModel actualizado = inventarioRepository.save(inventario);

        // Alerta si el stock queda por debajo del mínimo
        if (actualizado.getCantidad() < actualizado.getStockMinimo()) {
            log.warn("ALERTA: Stock bajo para producto id: {}. Cantidad actual: {}, Mínimo: {}",
                    productoId, actualizado.getCantidad(), actualizado.getStockMinimo());
        }

        return mapearAResponse(actualizado);
    }

    public void eliminar(Long id) {
        log.info("Eliminando inventario con id: {}", id);
        if (!inventarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventario no encontrado con id: " + id);
        }
        inventarioRepository.deleteById(id);
        log.info("Inventario con id {} eliminado", id);
    }

    // Consulta al product-service para verificar que el producto existe
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto con id " + productoId + " no existe");
        } catch (Exception e) {
            log.warn("No se pudo validar el producto con product-service: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con el servicio de productos");
        }
    }

    private DtoInventarioResponse mapearAResponse(InventarioModel model) {
        boolean stockBajo = model.getCantidad() < model.getStockMinimo();
        return new DtoInventarioResponse(
                model.getIdInventario(),
                model.getProductoId(),
                model.getCantidad(),
                model.getStockMinimo(),
                stockBajo,
                model.getFechaActualizacion()
        );
    }
}
