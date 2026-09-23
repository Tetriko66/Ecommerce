package cl.duoc.MicroServicioNotificaciones.service;

import cl.duoc.MicroServicioNotificaciones.dto.request.DtoNotificacionRequest;
import cl.duoc.MicroServicioNotificaciones.dto.response.DtoNotificacionResponse;
import cl.duoc.MicroServicioNotificaciones.model.EstadoNotificacion;
import cl.duoc.MicroServicioNotificaciones.model.NotificacionModel;
import cl.duoc.MicroServicioNotificaciones.model.TipoNotificacion;
import cl.duoc.MicroServicioNotificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// Indica que esta clase es un servicio de Spring
@Service

// Genera constructor con los atributos final
@RequiredArgsConstructor
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    // Inyecta el repositorio de notificaciones
    private final NotificacionRepository notificacionRepository;

    // Retorna todas las notificaciones registradas
    public List<DtoNotificacionResponse> obtenerTodas() {
        log.info("Obteniendo todas las notificaciones");
        List<NotificacionModel> lista = notificacionRepository.findAll();
        log.info("Se encontraron {} notificaciones", lista.size());
        return lista.stream().map(this::mapearAResponse).collect(Collectors.toList());
    }

    // Retorna una notificación por su ID
    public DtoNotificacionResponse obtenerPorId(Long id) {
        log.info("Buscando notificación con id: {}", id);
        NotificacionModel notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Notificación con id {} no encontrada", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Notificación no encontrada con id: " + id);
                });
        return mapearAResponse(notificacion);
    }

    // Retorna notificaciones filtradas por estado
    public List<DtoNotificacionResponse> obtenerPorEstado(EstadoNotificacion estado) {
        log.info("Buscando notificaciones con estado: {}", estado);
        return notificacionRepository.findByEstado(estado)
                .stream().map(this::mapearAResponse).collect(Collectors.toList());
    }

    // Crea y registra una nueva notificación con estado PENDIENTE
    public DtoNotificacionResponse crear(DtoNotificacionRequest request) {
        log.info("Creando notificación de tipo {} para: {}", request.getTipo(), request.getDestinatario());

        NotificacionModel notificacion = new NotificacionModel();
        notificacion.setTipo(request.getTipo());
        notificacion.setDestinatario(request.getDestinatario());
        notificacion.setAsunto(request.getAsunto());
        notificacion.setMensaje(request.getMensaje());
        notificacion.setEstado(EstadoNotificacion.PENDIENTE);
        notificacion.setFechaCreacion(LocalDateTime.now());
        notificacion.setFechaEnvio(null);

        NotificacionModel guardada = notificacionRepository.save(notificacion);
        log.info("Notificación creada con id: {}", guardada.getIdNotificacion());
        return mapearAResponse(guardada);
    }

    // Marca una notificación como ENVIADO y registra la fecha de envío
    public DtoNotificacionResponse marcarComoEnviada(Long id) {
        log.info("Marcando notificación id {} como ENVIADO", id);
        NotificacionModel notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Notificación no encontrada con id: " + id));

        notificacion.setEstado(EstadoNotificacion.ENVIADO);
        notificacion.setFechaEnvio(LocalDateTime.now());
        NotificacionModel actualizada = notificacionRepository.save(notificacion);
        log.info("Notificación id {} marcada como ENVIADO", id);
        return mapearAResponse(actualizada);
    }

    // Marca una notificación como FALLIDO
    public DtoNotificacionResponse marcarComoFallida(Long id) {
        log.info("Marcando notificación id {} como FALLIDO", id);
        NotificacionModel notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Notificación no encontrada con id: " + id));

        notificacion.setEstado(EstadoNotificacion.FALLIDO);
        NotificacionModel actualizada = notificacionRepository.save(notificacion);
        log.info("Notificación id {} marcada como FALLIDO", id);
        return mapearAResponse(actualizada);
    }

    // Elimina una notificación por su ID
    public void eliminar(Long id) {
        log.info("Eliminando notificación con id: {}", id);
        if (!notificacionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Notificación no encontrada con id: " + id);
        }
        notificacionRepository.deleteById(id);
        log.info("Notificación con id {} eliminada", id);
    }

    // Convierte un modelo de BD a un DTO de respuesta
    private DtoNotificacionResponse mapearAResponse(NotificacionModel model) {
        return new DtoNotificacionResponse(
                model.getIdNotificacion(),
                model.getTipo(),
                model.getDestinatario(),
                model.getAsunto(),
                model.getMensaje(),
                model.getEstado(),
                model.getFechaCreacion(),
                model.getFechaEnvio()
        );
    }
}
