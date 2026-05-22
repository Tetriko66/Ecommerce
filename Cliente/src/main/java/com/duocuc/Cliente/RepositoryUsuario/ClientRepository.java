package com.duocuc.Cliente.RepositoryCliente;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duocuc.Cliente.UsuarioModel.Usuario;

@Repository
public interface ClientRepository extends JpaRepository<Usuario, Long> {
        // Buscar cliente por email
    Optional<Usuario> findByEmail(String email);

    // Buscar clientes por apellido
    List<Usuario> findByApellido(String apellido);

    // Buscar clientes cuyo nombre contenga un texto (ej: "Vic")
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    // Buscar clientes por teléfono exacto
    Optional<Usuario> findByTelefono(String telefono);

    // Contar clientes por apellido
    long countByApellido(String apellido);

    // Verificar si existe un cliente con un email
    boolean existsByEmail(String email);

    Optional<Usuario> findById(Long id);

    void deleteById(Long id);
}
