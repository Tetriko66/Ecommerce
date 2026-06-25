package cl.duoc.resena.service;

import cl.duoc.resena.dto.request.DtoResenaRequest;
import cl.duoc.resena.dto.response.DtoResenaResponse;
import cl.duoc.resena.model.ResenaModel;
import cl.duoc.resena.repository.ResenaRepository;
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
public class ResenaService {

    private static final Logger log = LoggerFactory.getLogger(ResenaService.class);

    private final ResenaRepository resenaRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${usuario.service.url}")
    private String usuarioServiceUrl;

    @Value("${producto.service.url}")
    private String productoServiceUrl;

    public List<DtoResenaResponse> obtenerTodos() {
        log.info("Obteniendo todas las reseñas");
        return resenaRepository.findAll().stream()
                .map(this::mapearAResponse).collect(Collectors.toList());
    }

    public DtoResenaResponse obtenerPorId(Long id) {
        log.info("Buscando reseña con id: {}", id);
        ResenaModel resena = resenaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Reseña con id {} no encontrada", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Reseña no encontrada con id: " + id);
                });
        return mapearAResponse(resena);
    }

    public List<DtoResenaResponse> obtenerPorProducto(Long productoId) {
        log.info("Buscando reseñas del producto id: {}", productoId);
        return resenaRepository.findByProductoId(productoId).stream()
                .map(this::mapearAResponse).collect(Collectors.toList());
    }

    public List<DtoResenaResponse> obtenerPorUsuario(Long usuarioId) {
        log.info("Buscando reseñas del usuario id: {}", usuarioId);
        return resenaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapearAResponse).collect(Collectors.toList());
    }

    public Double obtenerPromedioProducto(Long productoId) {
        log.info("Calculando promedio de calificaciones para producto id: {}", productoId);
        Double promedio = resenaRepository.calcularPromedioCalificacion(productoId);
        log.info("Promedio para producto {}: {}", productoId, promedio);
        return promedio != null ? promedio : 0.0;
    }

    public DtoResenaResponse crear(DtoResenaRequest request) {
        log.info("Creando reseña del usuario {} para producto {}", request.getUsuarioId(), request.getProductoId());

        // Valida que usuario y producto existan en sus servicios
        validarUsuarioExiste(request.getUsuarioId());
        validarProductoExiste(request.getProductoId());

        // Un usuario solo puede reseñar un producto una vez
        if (resenaRepository.existsByUsuarioIdAndProductoId(request.getUsuarioId(), request.getProductoId())) {
            log.error("El usuario {} ya reseñó el producto {}", request.getUsuarioId(), request.getProductoId());
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El usuario ya tiene una reseña para este producto");
        }

        ResenaModel resena = new ResenaModel();
        resena.setUsuarioId(request.getUsuarioId());
        resena.setProductoId(request.getProductoId());
        resena.setCalificacion(request.getCalificacion());
        resena.setComentario(request.getComentario());
        resena.setFechaResena(LocalDateTime.now());

        ResenaModel guardada = resenaRepository.save(resena);
        log.info("Reseña creada con id: {}", guardada.getIdResena());
        return mapearAResponse(guardada);
    }

    public DtoResenaResponse actualizar(Long id, DtoResenaRequest request) {
        log.info("Actualizando reseña con id: {}", id);
        ResenaModel resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reseña no encontrada con id: " + id));

        resena.setCalificacion(request.getCalificacion());
        resena.setComentario(request.getComentario());
        ResenaModel actualizada = resenaRepository.save(resena);
        log.info("Reseña {} actualizada", id);
        return mapearAResponse(actualizada);
    }

    public void eliminar(Long id) {
        log.info("Eliminando reseña con id: {}", id);
        if (!resenaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reseña no encontrada con id: " + id);
        }
        resenaRepository.deleteById(id);
        log.info("Reseña {} eliminada", id);
    }

    private void validarUsuarioExiste(Long usuarioId) {
        try {
            webClientBuilder.build()
                    .get()
                    .uri(usuarioServiceUrl + "/api/usuarios/" + usuarioId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario con id " + usuarioId + " no existe");
        } catch (Exception e) {
            log.warn("No se pudo conectar con user-service: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con el servicio de usuarios");
        }
    }

    private void validarProductoExiste(Long productoId) {
        try {
            webClientBuilder.build()
                    .get()
                    .uri(productoServiceUrl + "/api/v1/productos/" + productoId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto con id " + productoId + " no existe");
        } catch (Exception e) {
            log.warn("No se pudo conectar con product-service: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con el servicio de productos");
        }
    }

    private DtoResenaResponse mapearAResponse(ResenaModel model) {
        return new DtoResenaResponse(
                model.getIdResena(),
                model.getUsuarioId(),
                model.getProductoId(),
                model.getCalificacion(),
                model.getComentario(),
                model.getFechaResena()
        );
    }
}
