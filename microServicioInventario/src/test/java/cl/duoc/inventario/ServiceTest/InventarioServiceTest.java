package cl.duoc.inventario.ServiceTest;

import cl.duoc.inventario.dto.request.DtoAjusteStockRequest;
import cl.duoc.inventario.dto.response.DtoInventarioResponse;
import cl.duoc.inventario.model.InventarioModel;
import cl.duoc.inventario.repository.InventarioRepository;
import cl.duoc.inventario.service.InventarioService;
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
public class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private InventarioService inventarioService;

    private InventarioModel inventario;

    @BeforeEach
    void setUp() {
        inventario = new InventarioModel();
        inventario.setIdInventario(1L);
        inventario.setProductoId(10L);
        inventario.setCantidad(20);
        inventario.setStockMinimo(5);
        inventario.setFechaActualizacion(LocalDateTime.now());
    }

    @Test
    void obtenerTodosDevuelveLista() {
        // Given: el repositorio devuelve un registro de inventario
        when(inventarioRepository.findAll()).thenReturn(List.of(inventario));

        // When: se obtienen todos los inventarios
        List<DtoInventarioResponse> resultado = inventarioService.obtenerTodos();

        // Then: la lista tiene un item con producto id 10
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProductoId()).isEqualTo(10L);
    }

    @Test
    void obtenerPorIdDevuelveInventario() {
        // Given: existe inventario con id 1 y cantidad 20
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));

        // When: se busca por id
        DtoInventarioResponse resultado = inventarioService.obtenerPorId(1L);

        // Then: devuelve la cantidad correcta
        assertThat(resultado.getCantidad()).isEqualTo(20);
    }

    @Test
    void obtenerPorIdNoEncontradoLanzaExcepcion() {
        // Given: no existe inventario con id 99
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then: debe lanzar excepción de no encontrado
        assertThatThrownBy(() -> inventarioService.obtenerPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Inventario no encontrado con id: 99");
    }

    @Test
    void agregarStockSumaCantidad() {
        // Given: inventario con 20 unidades y se agregan 5
        DtoAjusteStockRequest request = new DtoAjusteStockRequest(5);
        when(inventarioRepository.findByProductoId(10L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(InventarioModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: se agrega stock al producto
        DtoInventarioResponse resultado = inventarioService.agregarStock(10L, request);

        // Then: la cantidad queda en 25
        assertThat(resultado.getCantidad()).isEqualTo(25);
    }

    @Test
    void reducirStockInsuficienteLanzaExcepcion() {
        // Given: inventario con 20 unidades pero se piden reducir 50
        DtoAjusteStockRequest request = new DtoAjusteStockRequest(50);
        when(inventarioRepository.findByProductoId(10L)).thenReturn(Optional.of(inventario));

        // When / Then: debe lanzar excepción por stock insuficiente
        assertThatThrownBy(() -> inventarioService.reducirStock(10L, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Stock insuficiente");
    }
}
