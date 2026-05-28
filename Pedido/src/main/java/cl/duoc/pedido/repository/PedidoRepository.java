package cl.duoc.pedido.repository;

import cl.duoc.pedido.model.PedidoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoModel, Long> {

    List<PedidoModel> findByUsuarioId(Long usuarioId);

    List<PedidoModel> findByEstado(String estado);
}
