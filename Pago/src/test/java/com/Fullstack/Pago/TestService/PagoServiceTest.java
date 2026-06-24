package com.Fullstack.Pago.TestService;

import com.Fullstack.Pago.DTO.Request;
import com.Fullstack.Pago.DTO.Response;
import com.Fullstack.Pago.Model.ModelPago;
import com.Fullstack.Pago.Repository.PagoRepository;
import com.Fullstack.Pago.Service.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private PagoService pagoService;

    private ModelPago pago;

    @BeforeEach
    void setUp() {
        pago = ModelPago.builder()
                .id(1L)
                .usuarioId(1L)
                .pedidoId(10L)
                .monto(5000.0)
                .fechaPago(LocalDateTime.now())
                .build();
    }

    @Test
    void registrarPagoDevuelvePagoCreado() {
        // Given: un request válido y el repositorio simula guardar el pago con id 1
        Request request = Request.builder()
                .usuarioId(1L)
                .pedidoId(10L)
                .monto(5000.0)
                .build();

        when(pagoRepository.save(any(ModelPago.class))).thenAnswer(invocation -> {
            ModelPago guardado = invocation.getArgument(0);
            guardado.setId(1L);
            guardado.setFechaPago(LocalDateTime.now());
            return guardado;
        });

        // When: se registra el pago
        Response resultado = pagoService.registrarPago(request);

        // Then: la respuesta contiene los datos del pago creado
        assertThat(resultado.getMonto()).isEqualTo(5000.0);
        assertThat(resultado.getUsuarioId()).isEqualTo(1L);
    }

    @Test
    void listarPagosDevuelveLista() {
        // Given: el repositorio devuelve un pago
        when(pagoRepository.findAll()).thenReturn(List.of(pago));

        // When: se listan los pagos
        List<Response> resultado = pagoService.listarPagos();

        // Then: la lista tiene un elemento con el pedido esperado
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPedidoId()).isEqualTo(10L);
    }

    @Test
    void actualizarMontoDevuelvePagoActualizado() {
        // Given: existe un pago con id 1
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(ModelPago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: se actualiza el monto a 7500
        Response resultado = pagoService.actualizarMonto(1L, 7500.0);

        // Then: el monto queda actualizado
        assertThat(resultado.getMonto()).isEqualTo(7500.0);
    }

    @Test
    void actualizarMontoPagoNoEncontradoLanzaExcepcion() {
        // Given: no existe pago con id 99
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then: al actualizar debe lanzar excepción
        assertThatThrownBy(() -> pagoService.actualizarMonto(99L, 1000.0))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pago no encontrado");
    }

    @Test
    void eliminarPagoEliminaDelRepositorio() {
        // When: se elimina el pago con id 1
        pagoService.eliminarPago(1L);

        // Then: el repositorio recibe la orden de borrar
        verify(pagoRepository).deleteById(1L);
    }
}
