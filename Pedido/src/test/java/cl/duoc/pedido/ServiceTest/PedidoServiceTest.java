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
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

        List<DtoPedidoResponse> resultado = pedidoService.obtenerTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo("PAGADO");
    }

    @Test
    void obtenerPorIdDevuelvePedido() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        DtoPedidoResponse resultado = pedidoService.obtenerPorId(1L);

        assertThat(resultado.getIdPedido()).isEqualTo(1L);
        assertThat(resultado.getMontoTotal()).isEqualTo(5000.0);
    }

    @Test
    void obtenerPorIdNoEncontradoLanzaExcepcion() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.obtenerPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Pedido no encontrado con id: 99");
    }

    @Test
    void eliminarExitoso() {
        when(pedidoRepository.existsById(1L)).thenReturn(true);

        pedidoService.eliminar(1L);

        verify(pedidoRepository).deleteById(1L);
    }

    @Test
    void eliminarPedidoNoEncontradoLanzaExcepcion() {
        when(pedidoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> pedidoService.eliminar(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Pedido no encontrado con id: 99");
    }
}