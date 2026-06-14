package com.projecto.Usuario.UsuarioService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.projecto.Usuario.UsuarioDTO.UsuarioRequest;
import com.projecto.Usuario.UsuarioDTO.UsuarioResponse;
import com.projecto.Usuario.UsuarioModel.Usuario;
import com.projecto.Usuario.UsuarioRepository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Crear usuario a partir de UsuarioRequest
    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El correo ya existe en la base de datos");
        }
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe en la base de datos");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(request.getPassword());
        usuario.setEmail(request.getEmail());

        Usuario guardado = usuarioRepository.save(usuario);

        return new UsuarioResponse(guardado.getId(), guardado.getUsername(), guardado.getEmail());
    }

    // Listar todos los usuarios
    public List<UsuarioResponse> obtenerUsuarios() {
    List<UsuarioResponse> respuestas = new ArrayList<>();
    for (Usuario u : usuarioRepository.findAll()) {
        respuestas.add(new UsuarioResponse(u.getId(), u.getUsername(), u.getEmail()));
    }
    return respuestas;
    }

    // Obtener usuario por ID
    public UsuarioResponse obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        return new UsuarioResponse(usuario.getId(), usuario.getUsername(), usuario.getEmail());
    }

    // Actualizar usuario
    public UsuarioResponse actualizarUsuario(Long id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        usuario.setUsername(request.getUsername());
        usuario.setPassword(request.getPassword());
        usuario.setEmail(request.getEmail());

        Usuario actualizado = usuarioRepository.save(usuario);
        return new UsuarioResponse(actualizado.getId(), actualizado.getUsername(), actualizado.getEmail());
    }

    // Eliminar usuario
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
