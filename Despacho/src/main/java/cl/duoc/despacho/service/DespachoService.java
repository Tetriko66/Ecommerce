package cl.duoc.despacho.service;

import cl.duoc.despacho.dto.request.DtoDespachoRequest;
import cl.duoc.despacho.dto.response.DtoDespachoResponse;
import cl.duoc.despacho.model.DespachoModel;
import cl.duoc.despacho.repository.DespachoRepository;
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
public class DespachoService {

    private static final Logger log = LoggerFactory.getLogger(DespachoService.class);

    private final DespachoRepository despachoRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${pedido.service.url}")
    private String pedidoServiceUrl;

    public List<DtoDespachoResponse> obtenerTodos() {
        log.info("Obteniendo todos los despachos");
        return despachoRepository.findAll().stream()
                .map(this::mapearAResponse).collect(Collectors.toList());
    }

    public DtoDespachoResponse obtenerPorId(Long id) {
        log.info("Buscando despacho con id: {}", id);
        DespachoModel despacho = despachoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Despacho con id {} no encontrado", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Despacho no encontrado con id: " + id);
                });
        return mapearAResponse(despacho);
    }

    public DtoDespachoResponse obtenerPorPedido(Long pedidoId) {
        log.info("Buscando despacho para pedido id: {}", pedidoId);
        DespachoModel despacho = despachoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe despacho para el pedido con id: " + pedidoId));
        return mapearAResponse(despacho);
    }

    public List<DtoDespachoResponse> obtenerPorEstado(String estado) {
        log.info("Buscando despachos con estado: {}", estado);
        return despachoRepository.findByEstado(estado.toUpperCase()).stream()
                .map(this::mapearAResponse).collect(Collectors.toList());
    }

    public DtoDespachoResponse crear(DtoDespachoRequest request, String authHeader) {
        log.info("Creando despacho para pedido id: {}", request.getPedidoId());

        // Valida que el pedido exista en el microservicio de pedidos
        validarPedidoExiste(request.getPedidoId());

        // Un pedido solo puede tener un despacho
        if (despachoRepository.existsByPedidoId(request.getPedidoId())) {
            log.error("Ya existe un despacho para el pedido id: {}", request.getPedidoId());
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un despacho para el pedido con id: " + request.getPedidoId());
        }

        // Actualiza el estado del pedido a DESPACHADO
        actualizarEstadoPedido(request.getPedidoId(), "DESPACHADO", authHeader);

        DespachoModel despacho = new DespachoModel();
        despacho.setPedidoId(request.getPedidoId());
        despacho.setDireccionEntrega(request.getDireccionEntrega());
        despacho.setEstado("PREPARANDO");
        despacho.setFechaDespacho(LocalDateTime.now());
        despacho.setFechaEntrega(null);

        DespachoModel guardado = despachoRepository.save(despacho);
        log.info("Despacho creado con id: {} para pedido id: {}", guardado.getIdDespacho(), guardado.getPedidoId());
        return mapearAResponse(guardado);
    }

    public DtoDespachoResponse actualizarEstado(Long id, String estado) {
        log.info("Actualizando estado del despacho {} a {}", id, estado);
        DespachoModel despacho = despachoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Despacho no encontrado con id: " + id));

        despacho.setEstado(estado.toUpperCase());

        if ("ENTREGADO".equalsIgnoreCase(estado)) {
            despacho.setFechaEntrega(LocalDateTime.now());
        }

        DespachoModel actualizado = despachoRepository.save(despacho);
        log.info("Estado del despacho {} actualizado a {}", id, actualizado.getEstado());
        return mapearAResponse(actualizado);
    }

    public void eliminar(Long id) {
        log.info("Eliminando despacho con id: {}", id);
        if (!despachoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Despacho no encontrado con id: " + id);
        }
        despachoRepository.deleteById(id);
        log.info("Despacho {} eliminado", id);
    }

    private void validarPedidoExiste(Long pedidoId) {
        try {
            log.info("Validando existencia del pedido id: {} en pedido-service", pedidoId);
            webClientBuilder.build()
                    .get()
                    .uri(pedidoServiceUrl + "/api/v1/pedidos/" + pedidoId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            log.info("Pedido id: {} validado correctamente", pedidoId);
        } catch (WebClientResponseException.NotFound e) {
            log.error("Pedido id: {} no existe en pedido-service", pedidoId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "El pedido con id " + pedidoId + " no existe");
        } catch (Exception e) {
            log.warn("No se pudo contactar pedido-service: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con el servicio de pedidos");
        }
    }

    private void actualizarEstadoPedido(Long pedidoId, String estado, String authHeader) {
        try {
            log.info("Actualizando estado del pedido id: {} a {}", pedidoId, estado);
            webClientBuilder.build()
                    .patch()
                    .uri(pedidoServiceUrl + "/api/v1/pedidos/" + pedidoId + "/estado?estado=" + estado)
                    .headers(h -> { if (authHeader != null) h.set("Authorization", authHeader); })
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            log.warn("No se pudo actualizar el estado del pedido {}: {}", pedidoId, e.getMessage());
        }
    }

    private DtoDespachoResponse mapearAResponse(DespachoModel model) {
        return new DtoDespachoResponse(
                model.getIdDespacho(),
                model.getPedidoId(),
                model.getDireccionEntrega(),
                model.getEstado(),
                model.getFechaDespacho(),
                model.getFechaEntrega()
        );
    }
}
