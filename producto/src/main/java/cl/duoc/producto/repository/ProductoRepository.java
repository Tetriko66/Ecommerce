package cl.duoc.producto.repository;

import cl.duoc.producto.model.ProductoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<ProductoModel, Long> {

    // Retorna todos los productos activos
    List<ProductoModel> findByActivoTrue();

    // Busca productos por categoría
    List<ProductoModel> findByCategoria(String categoria);

    // Busca productos activos por categoría
    List<ProductoModel> findByCategoriaAndActivoTrue(String categoria);
}
