package cl.duoc.pedido.ServiceTest;

import cl.duoc.pedido.dto.response.DtoPedidoResponse;
import cl.duoc.pedido.model.PedidoModel;
import cl.duoc.pedido.repository.PedidoRepository;
import cl.duoc.pedido.service.PedidoService;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private PedidoService pedidoService;

    private PedidoModel pedido;

    @BeforeEach
    void setUp() {
        pedido = new PedidoModel(
                1L, 1L, 10L, 2, 5000.0,
                "TARJETA", "PAGADO", LocalDateTime.now()
        );
    }

    @Test
    void obtenerTodosDevuelveLista() {
        // Given: el repositorio devuelve un pedido
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

        // When: se obtienen todos los pedidos
        List<DtoPedidoResponse> resultado = pedidoService.obtenerTodos();

        // Then: la lista tiene un pedido con estado PAGADO
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo("PAGADO");
    }

    @Test
    void obtenerPorIdDevuelvePedido() {
        // Given: existe un pedido con id 1
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // When: se busca por id
        DtoPedidoResponse resultado = pedidoService.obtenerPorId(1L);

        // Then: devuelve id y monto correctos
        assertThat(resultado.getIdPedido()).isEqualTo(1L);
        assertThat(resultado.getMontoTotal()).isEqualTo(5000.0);
    }

    @Test
    void obtenerPorIdNoEncontradoLanzaExcepcion() {
        // Given: no existe pedido con id 99
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then: debe lanzar excepción de no encontrado
        assertThatThrownBy(() -> pedidoService.obtenerPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Pedido no encontrado con id: 99");
    }

    @Test
    void eliminarExitoso() {
        // Given: existe un pedido con id 1
        when(pedidoRepository.existsById(1L)).thenReturn(true);

        // When: se elimina el pedido
        pedidoService.eliminar(1L);

        // Then: el repositorio borra por id
        verify(pedidoRepository).deleteById(1L);
    }

    @Test
    void eliminarPedidoNoEncontradoLanzaExcepcion() {
        // Given: no existe pedido con id 99
        when(pedidoRepository.existsById(99L)).thenReturn(false);

        // When / Then: debe lanzar excepción de no encontrado
        assertThatThrownBy(() -> pedidoService.eliminar(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Pedido no encontrado con id: 99");
    }
}
