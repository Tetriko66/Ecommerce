package cl.duoc.MicroServicioNotificaciones.ServiceTest;

import cl.duoc.MicroServicioNotificaciones.dto.request.DtoNotificacionRequest;
import cl.duoc.MicroServicioNotificaciones.dto.response.DtoNotificacionResponse;
import cl.duoc.MicroServicioNotificaciones.model.EstadoNotificacion;
import cl.duoc.MicroServicioNotificaciones.model.NotificacionModel;
import cl.duoc.MicroServicioNotificaciones.model.TipoNotificacion;
import cl.duoc.MicroServicioNotificaciones.repository.NotificacionRepository;
import cl.duoc.MicroServicioNotificaciones.service.NotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    private NotificacionModel notificacion;

    @BeforeEach
    void setUp() {
        notificacion = new NotificacionModel();
        notificacion.setIdNotificacion(1L);
        notificacion.setTipo(TipoNotificacion.EMAIL);
        notificacion.setDestinatario("user@example.com");
        notificacion.setAsunto("Pedido confirmado");
        notificacion.setMensaje("Tu pedido fue registrado");
        notificacion.setEstado(EstadoNotificacion.PENDIENTE);
        notificacion.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    void obtenerTodasDevuelveLista() {
        // Given: el repositorio devuelve una notificación
        when(notificacionRepository.findAll()).thenReturn(List.of(notificacion));

        // When: se obtienen todas las notificaciones
        List<DtoNotificacionResponse> resultado = notificacionService.obtenerTodas();

        // Then: la lista tiene una notificación con destinatario correcto
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDestinatario()).isEqualTo("user@example.com");
    }

    @Test
    void obtenerPorIdDevuelveNotificacion() {
        // Given: existe una notificación con id 1 en estado PENDIENTE
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));

        // When: se busca por id
        DtoNotificacionResponse resultado = notificacionService.obtenerPorId(1L);

        // Then: devuelve estado PENDIENTE
        assertThat(resultado.getEstado()).isEqualTo(EstadoNotificacion.PENDIENTE);
    }

    @Test
    void obtenerPorIdNoEncontradaLanzaExcepcion() {
        // Given: no existe notificación con id 99
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then: debe lanzar excepción de no encontrada
        assertThatThrownBy(() -> notificacionService.obtenerPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Notificación no encontrada con id: 99");
    }

    @Test
    void marcarComoEnviadaCambiaEstado() {
        // Given: existe una notificación pendiente
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(any(NotificacionModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: se marca como enviada
        DtoNotificacionResponse resultado = notificacionService.marcarComoEnviada(1L);

        // Then: el estado cambia a ENVIADO y se registra fecha de envío
        assertThat(resultado.getEstado()).isEqualTo(EstadoNotificacion.ENVIADO);
        assertThat(resultado.getFechaEnvio()).isNotNull();
    }

    @Test
    void crearNotificacionQuedaEnEstadoPendiente() {
        // Given: un request válido y el repositorio guarda con id 2
        DtoNotificacionRequest request = new DtoNotificacionRequest(
                TipoNotificacion.EMAIL,
                "nuevo@example.com",
                "Bienvenida",
                "Hola, bienvenido al ecommerce"
        );
        when(notificacionRepository.save(any(NotificacionModel.class))).thenAnswer(invocation -> {
            NotificacionModel guardada = invocation.getArgument(0);
            guardada.setIdNotificacion(2L);
            return guardada;
        });

        // When: se crea la notificación
        DtoNotificacionResponse resultado = notificacionService.crear(request);

        // Then: queda en estado PENDIENTE y se guarda en el repositorio
        assertThat(resultado.getEstado()).isEqualTo(EstadoNotificacion.PENDIENTE);
        verify(notificacionRepository).save(any(NotificacionModel.class));
    }
}
