package com.Fullstack.Pago.Service;

import org.springframework.stereotype.Service;

import com.Fullstack.Pago.DTO.Request;
import com.Fullstack.Pago.DTO.Response;
import com.Fullstack.Pago.Model.ModelPago;
import com.Fullstack.Pago.Repository.PagoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagoService {
    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public Response registrarPago(Request request) {
        ModelPago pago = ModelPago.builder()
                .usuarioId(request.getUsuarioId())
                .pedidoId(request.getPedidoId())
                .monto(request.getMonto())
                .build();
        ModelPago saved = pagoRepository.save(pago);
        return toResponse(saved);
    }

    public List<Response> listarPagos() {
        return pagoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Response actualizarMonto(Long id, Double monto) {
        ModelPago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        pago.setMonto(monto);
        return toResponse(pagoRepository.save(pago));
    }

    public void eliminarPago(Long id) {
        pagoRepository.deleteById(id);
    }

    private Response toResponse(ModelPago pago) {
        return Response.builder()
                .id(pago.getId())
                .usuarioId(pago.getUsuarioId())
                .pedidoId(pago.getPedidoId())
                .monto(pago.getMonto())
                .fechaCreacion(pago.getFechaPago())
                .build();
    }
}
