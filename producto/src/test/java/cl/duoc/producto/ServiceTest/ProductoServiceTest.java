package cl.duoc.producto.ServiceTest;

import cl.duoc.producto.dto.response.DtoProductoResponse;
import cl.duoc.producto.model.ProductoModel;
import cl.duoc.producto.repository.ProductoRepository;
import cl.duoc.producto.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private ProductoModel producto;

    @BeforeEach
    void setUp() {
        producto = new ProductoModel();
        producto.setIdProducto(1L);
        producto.setNombre("Laptop");
        producto.setDescripcion("Laptop gamer");
        producto.setPrecio(new BigDecimal("999990"));
        producto.setCategoria("Tecnologia");
        producto.setStock(10);
        producto.setActivo(true);
        producto.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    void obtenerTodosDevuelveLista() {
        // Given: el repositorio devuelve un producto
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        // When: se obtienen todos los productos
        List<DtoProductoResponse> resultado = productoService.obtenerTodos();

        // Then: la lista tiene un producto con nombre Laptop
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Laptop");
    }

    @Test
    void obtenerPorIdDevuelveProducto() {
        // Given: existe un producto con id 1
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When: se busca por id
        DtoProductoResponse resultado = productoService.obtenerPorId(1L);

        // Then: devuelve id y precio correctos
        assertThat(resultado.getIdProducto()).isEqualTo(1L);
        assertThat(resultado.getPrecio()).isEqualByComparingTo(new BigDecimal("999990"));
    }

    @Test
    void obtenerPorIdNoEncontradoLanzaExcepcion() {
        // Given: no existe producto con id 99
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then: debe lanzar excepción de no encontrado
        assertThatThrownBy(() -> productoService.obtenerPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Producto no encontrado con id: 99");
    }

    @Test
    void desactivarMarcaProductoComoInactivo() {
        // Given: existe un producto activo con id 1
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(ProductoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: se desactiva el producto
        productoService.desactivar(1L);

        // Then: el producto queda inactivo y se guarda en el repositorio
        verify(productoRepository).save(any(ProductoModel.class));
        assertThat(producto.getActivo()).isFalse();
    }

    @Test
    void eliminarProductoNoEncontradoLanzaExcepcion() {
        // Given: no existe producto con id 99
        when(productoRepository.existsById(99L)).thenReturn(false);

        // When / Then: debe lanzar excepción de no encontrado
        assertThatThrownBy(() -> productoService.eliminar(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Producto no encontrado con id: 99");
    }
}
