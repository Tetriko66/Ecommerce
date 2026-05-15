package com.duocuc.Cliente.RepositoryCliente;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duocuc.Cliente.ClienteModel.Cliente;

@Repository
public interface ClientRepository extends JpaRepository<Cliente, Long> {
        // Buscar cliente por email
    Optional<Cliente> findByEmail(String email);

    // Buscar clientes por apellido
    List<Cliente> findByApellido(String apellido);

    // Buscar clientes cuyo nombre contenga un texto (ej: "Vic")
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    // Buscar clientes por teléfono exacto
    Optional<Cliente> findByTelefono(String telefono);

    // Contar clientes por apellido
    long countByApellido(String apellido);

    // Verificar si existe un cliente con un email
    boolean existsByEmail(String email);

    Optional<Cliente> findById(Long id);

    void deleteById(Long id);
}
