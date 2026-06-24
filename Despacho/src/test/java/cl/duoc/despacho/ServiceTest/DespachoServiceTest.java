package cl.duoc.despacho.ServiceTest;

import cl.duoc.despacho.dto.response.DtoDespachoResponse;
import cl.duoc.despacho.model.DespachoModel;
import cl.duoc.despacho.repository.DespachoRepository;
import cl.duoc.despacho.service.DespachoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DespachoServiceTest {

    @Mock
    private DespachoRepository despachoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private DespachoService despachoService;

    private DespachoModel despacho;

    @BeforeEach
    void setUp() {
        despacho = new DespachoModel();
        despacho.setIdDespacho(1L);
        despacho.setPedidoId(10L);
        despacho.setDireccionEntrega("Av. Principal 123");
        despacho.setEstado("PREPARANDO");
        despacho.setFechaDespacho(LocalDateTime.now());
    }

    @Test
    void obtenerTodosDevuelveLista() {
        // Given: el repositorio devuelve un despacho
        when(despachoRepository.findAll()).thenReturn(List.of(despacho));

        // When: se obtienen todos los despachos
        List<DtoDespachoResponse> resultado = despachoService.obtenerTodos();

        // Then: la lista tiene un despacho con pedido id 10
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPedidoId()).isEqualTo(10L);
    }

    @Test
    void obtenerPorIdDevuelveDespacho() {
        // Given: existe un despacho con id 1 en estado PREPARANDO
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));

        // When: se busca por id
        DtoDespachoResponse resultado = despachoService.obtenerPorId(1L);

        // Then: devuelve el estado correcto
        assertThat(resultado.getEstado()).isEqualTo("PREPARANDO");
    }

    @Test
    void obtenerPorIdNoEncontradoLanzaExcepcion() {
        // Given: no existe despacho con id 99
        when(despachoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then: debe lanzar excepción de no encontrado
        assertThatThrownBy(() -> despachoService.obtenerPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Despacho no encontrado con id: 99");
    }

    @Test
    void actualizarEstadoDevuelveDespachoActualizado() {
        // Given: existe un despacho y el repositorio guarda los cambios
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
        when(despachoRepository.save(any(DespachoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: se actualiza el estado a en_camino
        DtoDespachoResponse resultado = despachoService.actualizarEstado(1L, "en_camino");

        // Then: el estado queda en mayúsculas EN_CAMINO
        assertThat(resultado.getEstado()).isEqualTo("EN_CAMINO");
    }

    @Test
    void eliminarDespachoNoEncontradoLanzaExcepcion() {
        // Given: no existe despacho con id 99
        when(despachoRepository.existsById(99L)).thenReturn(false);

        // When / Then: debe lanzar excepción de no encontrado
        assertThatThrownBy(() -> despachoService.eliminar(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Despacho no encontrado con id: 99");
    }
}
