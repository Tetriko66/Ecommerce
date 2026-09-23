package cl.duoc.producto.service;

import cl.duoc.producto.dto.request.DtoProductoRequest;
import cl.duoc.producto.dto.response.DtoProductoResponse;
import cl.duoc.producto.model.ProductoModel;
import cl.duoc.producto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;

    public List<DtoProductoResponse> obtenerTodos() {
        log.info("Obteniendo todos los productos");
        List<ProductoModel> productos = productoRepository.findAll();
        log.info("Se encontraron {} productos", productos.size());
        return productos.stream().map(this::mapearAResponse).collect(Collectors.toList());
    }

    public List<DtoProductoResponse> obtenerActivos() {
        log.info("Obteniendo productos activos");
        return productoRepository.findByActivoTrue().stream()
                .map(this::mapearAResponse).collect(Collectors.toList());
    }

    public DtoProductoResponse obtenerPorId(Long id) {
        log.info("Buscando producto con id: {}", id);
        ProductoModel producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Producto con id {} no encontrado", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + id);
                });
        log.info("Producto encontrado: {}", producto.getNombre());
        return mapearAResponse(producto);
    }

    public List<DtoProductoResponse> obtenerPorCategoria(String categoria) {
        log.info("Buscando productos de categoría: {}", categoria);
        return productoRepository.findByCategoria(categoria).stream()
                .map(this::mapearAResponse).collect(Collectors.toList());
    }

    public DtoProductoResponse crear(DtoProductoRequest request) {
        log.info("Creando nuevo producto: {}", request.getNombre());
        ProductoModel producto = new ProductoModel();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setCategoria(request.getCategoria());
        producto.setStock(request.getStock());
        producto.setActivo(true);
        producto.setFechaCreacion(LocalDateTime.now());
        ProductoModel guardado = productoRepository.save(producto);
        log.info("Producto creado con id: {}", guardado.getIdProducto());
        return mapearAResponse(guardado);
    }

    public DtoProductoResponse actualizar(Long id, DtoProductoRequest request) {
        log.info("Actualizando producto con id: {}", id);
        ProductoModel producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Producto con id {} no encontrado para actualizar", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + id);
                });
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setCategoria(request.getCategoria());
        producto.setStock(request.getStock());
        ProductoModel actualizado = productoRepository.save(producto);
        log.info("Producto con id {} actualizado correctamente", id);
        return mapearAResponse(actualizado);
    }

    // Desactiva un producto en lugar de eliminarlo físicamente
    public void desactivar(Long id) {
        log.info("Desactivando producto con id: {}", id);
        ProductoModel producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Producto con id {} no encontrado para desactivar", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + id);
                });
        producto.setActivo(false);
        productoRepository.save(producto);
        log.info("Producto con id {} desactivado correctamente", id);
    }

    public void eliminar(Long id) {
        log.info("Eliminando producto con id: {}", id);
        if (!productoRepository.existsById(id)) {
            log.error("No se puede eliminar: producto con id {} no existe", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
        log.info("Producto con id {} eliminado correctamente", id);
    }

    private DtoProductoResponse mapearAResponse(ProductoModel model) {
        return new DtoProductoResponse(
                model.getIdProducto(),
                model.getNombre(),
                model.getDescripcion(),
                model.getPrecio(),
                model.getCategoria(),
                model.getStock(),
                model.getActivo(),
                model.getFechaCreacion()
        );
    }
}
