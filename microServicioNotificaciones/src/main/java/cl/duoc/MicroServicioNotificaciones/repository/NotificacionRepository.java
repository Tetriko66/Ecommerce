package cl.duoc.MicroServicioNotificaciones.repository;

import cl.duoc.MicroServicioNotificaciones.model.EstadoNotificacion;
import cl.duoc.MicroServicioNotificaciones.model.NotificacionModel;
import cl.duoc.MicroServicioNotificaciones.model.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<NotificacionModel, Long> {

    // Busca notificaciones por destinatario
    List<NotificacionModel> findByDestinatario(String destinatario);

    // Busca notificaciones por estado (ej: PENDIENTE, ENVIADO, FALLIDO)
    List<NotificacionModel> findByEstado(EstadoNotificacion estado);

    // Busca notificaciones por tipo (ej: EMAIL, SMS, PUSH)
    List<NotificacionModel> findByTipo(TipoNotificacion tipo);
}
