package cl.duoc.despacho.repository;

import cl.duoc.despacho.model.DespachoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DespachoRepository extends JpaRepository<DespachoModel, Long> {

    Optional<DespachoModel> findByPedidoId(Long pedidoId);

    List<DespachoModel> findByEstado(String estado);

    boolean existsByPedidoId(Long pedidoId);
}
