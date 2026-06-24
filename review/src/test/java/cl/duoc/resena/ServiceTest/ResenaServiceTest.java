package cl.duoc.resena.ServiceTest;

import cl.duoc.resena.dto.response.DtoResenaResponse;
import cl.duoc.resena.model.ResenaModel;
import cl.duoc.resena.repository.ResenaRepository;
import cl.duoc.resena.service.ResenaService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private ResenaService resenaService;

    private ResenaModel resena;

    @BeforeEach
    void setUp() {
        resena = new ResenaModel();
        resena.setIdResena(1L);
        resena.setUsuarioId(1L);
        resena.setProductoId(10L);
        resena.setCalificacion(5);
        resena.setComentario("Excelente producto");
        resena.setFechaResena(LocalDateTime.now());
    }

    @Test
    void obtenerTodosDevuelveLista() {
        // Given: el repositorio devuelve una reseña
        when(resenaRepository.findAll()).thenReturn(List.of(resena));

        // When: se obtienen todas las reseñas
        List<DtoResenaResponse> resultado = resenaService.obtenerTodos();

        // Then: la lista tiene una reseña con calificación 5
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCalificacion()).isEqualTo(5);
    }

    @Test
    void obtenerPorIdDevuelveResena() {
        // Given: existe una reseña con id 1
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        // When: se busca por id
        DtoResenaResponse resultado = resenaService.obtenerPorId(1L);

        // Then: devuelve el comentario correcto
        assertThat(resultado.getComentario()).isEqualTo("Excelente producto");
    }

    @Test
    void obtenerPorIdNoEncontradaLanzaExcepcion() {
        // Given: no existe reseña con id 99
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then: debe lanzar excepción de no encontrada
        assertThatThrownBy(() -> resenaService.obtenerPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Reseña no encontrada con id: 99");
    }

    @Test
    void obtenerPromedioProductoDevuelvePromedio() {
        // Given: el repositorio calcula promedio 4.5 para producto 10
        when(resenaRepository.calcularPromedioCalificacion(10L)).thenReturn(4.5);

        // When: se obtiene el promedio del producto
        Double resultado = resenaService.obtenerPromedioProducto(10L);

        // Then: devuelve 4.5
        assertThat(resultado).isEqualTo(4.5);
    }

    @Test
    void obtenerPromedioProductoSinResenasDevuelveCero() {
        // Given: no hay reseñas (promedio null)
        when(resenaRepository.calcularPromedioCalificacion(10L)).thenReturn(null);

        // When: se obtiene el promedio del producto
        Double resultado = resenaService.obtenerPromedioProducto(10L);

        // Then: devuelve 0.0 por defecto
        assertThat(resultado).isEqualTo(0.0);
    }
}
