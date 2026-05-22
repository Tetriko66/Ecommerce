package com.duocuc.Cliente.ServiceUsuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duocuc.Cliente.ClienteModel.Cliente;
import com.duocuc.Cliente.DTOCliente.ClienteRequest;
import com.duocuc.Cliente.DTOCliente.ClienteResponse;
import com.duocuc.Cliente.RepositoryCliente.ClientRepository;
import com.duocuc.Cliente.UsuarioModel.Usuario;

@Service
@Transactional
public class UsuarioService {

    //Inyectar el repositorio de clientes
    @Autowired
    private UsuarioRepository clienteRepository;

    //Metodo para crear un nuevo cliente usando DTO
    public UsuarioResponse crearUsuario(UsuarioRequest request){ {
        if (clienteRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("El correo ya existe en la base de datos");
        }
        
        Usuario cliente = new Usuario();
        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setEmail(request.getEmail());
        
        Usuario guardado = clienteRepository.save(cliente);

        return new UsuarioResponse(
            guardado.getId(),
            guardado.getNombre(),
            guardado.getApellido(),
            guardado.getEmail()
        );}
    }

    //Metodo para obtener todos los clientes como DTO response
    public List<UsuarioResponse> obtenerUsuarios(){
        List<UsuarioResponse> respuestas = new ArrayList<>();
        List<Usuario> usuarios = clienteRepository.findAll();
        for (Usuario u : usuarios) {
            UsuarioResponse response = new UsuarioResponse(
                u.getId(),
                u.getNombre(),
                u.getApellido(),
                u.getEmail()
            );
            respuestas.add(response);
        }
        return respuestas;
    }

    //Metodo para obtener un cliente por su ID
    public Optional<Usuario> obtenerUsuarioPorId(Long id){
        Usuario usuario =UsuarioRepository.findById(id);
                .orElsehrow (() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        return UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getApellido(), usuario.getEmail());
    }

    //Metodo para actualizar 
    public Usuario actualizarUsuario(Long Id , Usuario usuarioActualizado){
        Usuario usuario = clienteRepository.findById(Id)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + Id));

        return clienteRepository.save(cliente);
    }

    //Metodo para eliminar un cliente por su ID
    public void eliminarUsuario(Long id){
        clienteRepository.deleteById(id);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
}