package cl.duoc.pedido.service;

import cl.duoc.pedido.dto.request.DtoPedidoRequest;
import cl.duoc.pedido.dto.response.DtoPedidoResponse;
import cl.duoc.pedido.model.PedidoModel;
import cl.duoc.pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${inventario.service.url}")
    private String inventarioServiceUrl;

    @Value("${pago.service.url}")
    private String pagoServiceUrl;

    public List<DtoPedidoResponse> obtenerTodos() {
        log.info("Obteniendo todos los pedidos");
        return pedidoRepository.findAll().stream()
                .map(this::mapearAResponse).collect(Collectors.toList());
    }

    public DtoPedidoResponse obtenerPorId(Long id) {
        log.info("Buscando pedido con id: {}", id);
        PedidoModel pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Pedido con id {} no encontrado", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado con id: " + id);
                });
        return mapearAResponse(pedido);
    }

    public List<DtoPedidoResponse> obtenerPorUsuario(Long usuarioId) {
        log.info("Buscando pedidos del usuario id: {}", usuarioId);
        return pedidoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapearAResponse).collect(Collectors.toList());
    }

    public List<DtoPedidoResponse> obtenerPorEstado(String estado) {
        log.info("Buscando pedidos con estado: {}", estado);
        return pedidoRepository.findByEstado(estado.toUpperCase()).stream()
                .map(this::mapearAResponse).collect(Collectors.toList());
    }

    public DtoPedidoResponse crear(DtoPedidoRequest request, String authHeader) {
        log.info("Creando pedido para usuario {} - producto {} x{}", request.getUsuarioId(), request.getProductoId(), request.getCantidad());

        // 1. Valida stock y lo reduce en el microservicio de inventario
        reducirStockInventario(request.getProductoId(), request.getCantidad(), authHeader);

        // 2. Registra el pago en el microservicio de pago
        registrarPago(request.getUsuarioId(), request.getMontoTotal(), request.getMetodoPago(), authHeader);

        // 3. Guarda el pedido con estado PAGADO
        PedidoModel pedido = new PedidoModel();
        pedido.setUsuarioId(request.getUsuarioId());
        pedido.setProductoId(request.getProductoId());
        pedido.setCantidad(request.getCantidad());
        pedido.setMontoTotal(request.getMontoTotal());
        pedido.setMetodoPago(request.getMetodoPago());
        pedido.setEstado("PAGADO");
        pedido.setFechaPedido(LocalDateTime.now());

        PedidoModel guardado = pedidoRepository.save(pedido);
        log.info("Pedido creado con id: {}", guardado.getIdPedido());
        return mapearAResponse(guardado);
    }

    public DtoPedidoResponse actualizarEstado(Long id, String estado) {
        log.info("Actualizando estado del pedido {} a {}", id, estado);
        PedidoModel pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado con id: " + id));

        pedido.setEstado(estado.toUpperCase());
        PedidoModel actualizado = pedidoRepository.save(pedido);
        log.info("Estado del pedido {} actualizado a {}", id, actualizado.getEstado());
        return mapearAResponse(actualizado);
    }

    public void eliminar(Long id) {
        log.info("Eliminando pedido con id: {}", id);
        if (!pedidoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado con id: " + id);
        }
        pedidoRepository.deleteById(id);
        log.info("Pedido {} eliminado", id);
    }

    private void reducirStockInventario(Long productoId, Integer cantidad, String authHeader) {
        try {
            log.info("Reduciendo {} unidades del producto id: {} en inventario", cantidad, productoId);
            Map<String, Integer> body = Map.of("cantidad", cantidad);
            webClientBuilder.build()
                    .patch()
                    .uri(inventarioServiceUrl + "/api/v1/inventario/producto/" + productoId + "/reducir")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> { if (authHeader != null) h.set("Authorization", authHeader); })
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            log.info("Stock reducido correctamente para producto id: {}", productoId);
        } catch (WebClientResponseException.NotFound e) {
            log.error("No existe inventario para el producto id: {}", productoId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No hay inventario registrado para el producto con id " + productoId);
        } catch (WebClientResponseException.Conflict e) {
            log.error("Stock insuficiente para el producto id: {}", productoId);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Stock insuficiente para el producto con id " + productoId);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.warn("No se pudo contactar inventario-service: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con el servicio de inventario");
        }
    }

    private void registrarPago(Long usuarioId, Double monto, String metodoPago, String authHeader) {
        try {
            log.info("Registrando pago de ${} con método {} para usuario id: {}", monto, metodoPago, usuarioId);
            Map<String, Object> body = Map.of(
                    "usuarioId", usuarioId,
                    "pedidoId", 0,
                    "monto", monto,
                    "metodoPago", metodoPago
            );
            webClientBuilder.build()
                    .post()
                    .uri(pagoServiceUrl + "/api/v1/pagos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> { if (authHeader != null) h.set("Authorization", authHeader); })
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            log.info("Pago registrado correctamente");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.warn("No se pudo contactar pago-service: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con el servicio de pago");
        }
    }

    private DtoPedidoResponse mapearAResponse(PedidoModel model) {
        return new DtoPedidoResponse(
                model.getIdPedido(),
                model.getUsuarioId(),
                model.getProductoId(),
                model.getCantidad(),
                model.getMontoTotal(),
                model.getMetodoPago(),
                model.getEstado(),
                model.getFechaPedido()
        );
    }
}
