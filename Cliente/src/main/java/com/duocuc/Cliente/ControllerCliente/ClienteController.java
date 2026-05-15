package com.duocuc.Cliente.ControllerCliente;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duocuc.Cliente.ClienteModel.Cliente;
import com.duocuc.Cliente.ClienteModel.ClienteRequest;
import com.duocuc.Cliente.ClienteModel.ClienteResponse;
import com.duocuc.Cliente.ServiceCliente.ClienteService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    public ClienteService clienteservice;

    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente){
        Cliente nuevoCliente = clienteservice.crearCliente(cliente);

        return ResponseEntity.ok(nuevoCliente);
    }
    
    @GetMapping
    public ResponseEntity<List<Cliente>> listarCliente(){
        return ResponseEntity.ok(clienteservice.obtenerClientes());
    }
    
    @GetMapping({"/{id}"})
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long Id){
        return clienteservice.buscarPorId(Id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity <Cliente> actualizarCliente(@PathVariable long Id, @RequestBody Cliente cliente){
        Cliente actualizado = clienteservice.actualizarCliente(Id, cliente);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> EliminarCliente(@PathVariable Long Id){
        clienteservice.eliminarCliente(Id);
        return ResponseEntity.noContent().build();
    }
}
