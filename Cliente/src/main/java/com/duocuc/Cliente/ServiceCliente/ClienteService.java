package com.duocuc.Cliente.ServiceCliente;

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

@Service
@Transactional
public class ClienteService {

    //Inyectar el repositorio de clientes
    @Autowired
    private ClientRepository clienteRepository;

    //Metodo para crear un nuevo cliente usando DTO
    public ClienteResponse crearCliente(ClienteRequest request){ {
        if (clienteRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("El correo ya existe en la base de datos");
        }
        
        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setEmail(request.getEmail());
        
        Cliente guardado = clienteRepository.save(cliente);

        return new ClienteResponse(
            guardado.getId(),
            guardado.getNombre(),
            guardado.getApellido(),
            guardado.getEmail()
        );}
    }

    //Metodo para obtener todos los clientes como DTO response
    public List<ClienteResponse> obtenerClientes(){
        List<ClienteResponse> respuestas = new ArrayList<>();
        List<Cliente> clientes = clienteRepository.findAll();
        for (Cliente c : clientes) {
            ClienteResponse response = new ClienteResponse(
                c.getId(),
                c.getNombre(),
                c.getApellido(),
                c.getEmail()
            );
            respuestas.add(response);
        }
        return respuestas;
    }

    //Metodo para obtener un cliente por su ID
    public Optional<Cliente> obtenerClientePorId(Long id){
        Cliente cliente =clienteRepository.findById(id);
                .orElsehrow (() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
        return ClienteResponse(cliente.getId(), cliente.getNombre(), cliente.getApellido(), cliente.getEmail());
    }

    //Metodo para actualizar 
    public Cliente actualizarCliente(Long Id , Cliente clienteActualizado){
        Cliente cliente = clienteRepository.findById(Id)
        .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + Id));

        return clienteRepository.save(cliente);
    }

    //Metodo para eliminar un cliente por su ID
    public void eliminarCliente(Long id){
        clienteRepository.deleteById(id);
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }
}