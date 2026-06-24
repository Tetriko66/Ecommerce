package com.Fullstack.Carro.TestService;

import com.Fullstack.Carro.DTO.Response;
import com.Fullstack.Carro.Model.ModelCarro;
import com.Fullstack.Carro.Repository.RepositoryCarro;
import com.Fullstack.Carro.Service.ServiceCarro;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock
    private RepositoryCarro repository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private ServiceCarro service;

    private ModelCarro modelo;

    @BeforeEach
    void setUp() {
        modelo = new ModelCarro();
        modelo.setId(1L);
        modelo.setUsuarioId(1L);
        modelo.setProductoId(10L);
        modelo.setCantidad(2);
        modelo.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    void listarDevuelveLista() {
        // Given: el repositorio devuelve un item del carrito
        when(repository.findAll()).thenReturn(List.of(modelo));

        // When: se listan los items
        List<Response> resultado = service.listar();

        // Then: la lista tiene un elemento con producto y cantidad correctos
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProductoId()).isEqualTo(10L);
        assertThat(resultado.get(0).getCantidad()).isEqualTo(2);
    }

    @Test
    void listarDevuelveListaVacia() {
        // Given: el repositorio no tiene items
        when(repository.findAll()).thenReturn(List.of());

        // When: se listan los items
        List<Response> resultado = service.listar();

        // Then: la lista está vacía
        assertThat(resultado).isEmpty();
    }

    @Test
    void eliminarExitosoEliminaDelRepositorio() {
        // Given: existe un item del carrito con id 1
        when(repository.findById(1L)).thenReturn(Optional.of(modelo));

        // When: se elimina el item
        service.eliminar(1L);

        // Then: el repositorio borra el modelo
        verify(repository).delete(modelo);
    }

    @Test
    void eliminarCarritoQueNoExisteLanzaExcepcion() {
        // Given: no existe item con id 99
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When / Then: debe lanzar excepción de no encontrado
        assertThatThrownBy(() -> service.eliminar(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Carrito no encontrado con id 99");
    }

    @Test
    void listarConMultiplesElementosDevuelveTodos() {
        // Given: el repositorio devuelve dos items del carrito
        ModelCarro modelo2 = new ModelCarro();
        modelo2.setId(2L);
        modelo2.setUsuarioId(2L);
        modelo2.setProductoId(20L);
        modelo2.setCantidad(5);
        modelo2.setFechaCreacion(LocalDateTime.now());
        when(repository.findAll()).thenReturn(List.of(modelo, modelo2));

        // When: se listan los items
        List<Response> resultado = service.listar();

        // Then: la lista contiene ambos productos
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getProductoId()).isEqualTo(10L);
        assertThat(resultado.get(1).getProductoId()).isEqualTo(20L);
    }
}
