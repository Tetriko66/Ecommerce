package cl.duoc.inventario.repository;

import cl.duoc.inventario.model.InventarioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<InventarioModel, Long> {

    // Busca el inventario de un producto específico
    Optional<InventarioModel> findByProductoId(Long productoId);

    // Verifica si existe inventario para un producto
    boolean existsByProductoId(Long productoId);

    // Retorna registros con stock bajo (cantidad menor al mínimo definido)
    @org.springframework.data.jpa.repository.Query("SELECT i FROM InventarioModel i WHERE i.cantidad < i.stockMinimo")
    List<InventarioModel> findStockBajo();
}
