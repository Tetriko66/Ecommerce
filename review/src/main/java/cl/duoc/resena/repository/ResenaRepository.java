package cl.duoc.resena.repository;

import cl.duoc.resena.model.ResenaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResenaRepository extends JpaRepository<ResenaModel, Long> {

    List<ResenaModel> findByProductoId(Long productoId);

    List<ResenaModel> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndProductoId(Long usuarioId, Long productoId);

    // Calcula el promedio de calificaciones de un producto
    @Query("SELECT AVG(r.calificacion) FROM ResenaModel r WHERE r.productoId = :productoId")
    Double calcularPromedioCalificacion(Long productoId);
}
