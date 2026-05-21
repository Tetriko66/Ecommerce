package com.duocuc.Cliente.ControllerUsuario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duocuc.Usuario.ServiceUsuario.ClienteService;
import com.duocuc.Usuario.UsuarioModel.Usuario;
import com.duocuc.Usuario.UsuarioModel.ClienteRequest;
import com.duocuc.Usuario.UsuarioModel.ClienteResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/clientes")
public class UsuarioController {

    @Autowired
    public ClienteService clienteservice;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@RequestBody UsuarioRequest usuario){
        UsuarioResponse nuevoUsuario = ususarioservice.crearUsuario(usuario);

        return ResponseEntity.ok(nuevoUsuario);
    }
    
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios(){
        return ResponseEntity.ok(usuarioservice.obtenerUsuarios());
    }
    
    @GetMapping({"/{id}"})
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long Id){
        return usuarioservice.buscarPorId(Id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity <UsuarioResponse> actualizarUsuario(@PathVariable long Id, @RequestBody UsuarioRequest cliente){
        UsuarioResponse actualizado = usuarioservice.actualizarUsuario(Id, usuario);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> EliminarUsuario(@PathVariable Long Id){
        Usuarioservice.eliminarUsuario(Id);
        return ResponseEntity.noContent().build();
    }
}
