package com.projecto.Usuario.UsuarioService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.projecto.Usuario.UsuarioDTO.Request.Request;
import com.projecto.Usuario.UsuarioDTO.Response.Response;
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
    public Response crearUsuario(Request request) {
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

        return new Response(guardado.getId(), guardado.getUsername(), guardado.getEmail());
    }

    // Listar todos los usuarios
    public List<Response> obtenerUsuarios() {
    List<Response> respuestas = new ArrayList<>();
    for (Usuario u : usuarioRepository.findAll()) {
        respuestas.add(new Response(u.getId(), u.getUsername(), u.getEmail()));
    }
    return respuestas;
    }

    // Obtener usuario por ID
    public Response obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        return new Response(usuario.getId(), usuario.getUsername(), usuario.getEmail());
    }

    // Actualizar usuario
    public Response actualizarUsuario(Long id, Request request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        usuario.setUsername(request.getUsername());
        usuario.setPassword(request.getPassword());
        usuario.setEmail(request.getEmail());

        Usuario actualizado = usuarioRepository.save(usuario);
        return new Response(actualizado.getId(), actualizado.getUsername(), actualizado.getEmail());
    }

    // Eliminar usuario
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
