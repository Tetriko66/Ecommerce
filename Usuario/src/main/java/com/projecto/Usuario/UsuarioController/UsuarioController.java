
package com.projecto.Usuario.UsuarioController;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projecto.Usuario.UsuarioDTO.Request.Request;
import com.projecto.Usuario.UsuarioDTO.Response.Response;
import com.projecto.Usuario.UsuarioService.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Access;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuario API", description = "Operaciones CRUD para usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Crear usuario usando UsuarioRequest DTO
    @Operation(description = "Crea un nuevo usuario")
    @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @PostMapping
    public ResponseEntity<Response> crearUsuario(@Valid @RequestBody Request request) {
        return ResponseEntity.ok(usuarioService.crearUsuario(request));
    }

    // Listar todos los usuarios devolviendo UsuarioResponse DTO
    @Operation(description = "Obtiene la lista de todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping
    public ResponseEntity<List<Response>> obtenerUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerUsuarios());
    }

    // Obtener usuario por ID
    @Operation(description = "Obtiene un usuario por su ID")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado ")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @GetMapping("/{id}")
    public ResponseEntity<Response> obtenerUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerUsuarioPorId(id));
    }

    // Actualizar usuario con UsuarioRequest DTO
    @Operation(description = "Actualiza un usuario existente por su ID")
    @ApiResponse(responseCode = "200", description = "Usuario Actualizado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @PutMapping("/{id}")
    public ResponseEntity<Response> actualizarUsuario(@PathVariable Long id,
                                                             @Valid @RequestBody Request request) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, request));
    }

    // Eliminar usuario
    @Operation(description = "Elimina un usuario por su ID")
    @ApiResponse(responseCode = "200", description = "Usuario eliminado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
