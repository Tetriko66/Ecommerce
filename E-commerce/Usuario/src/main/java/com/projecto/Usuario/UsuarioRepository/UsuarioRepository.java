package com.projecto.Usuario.UsuarioRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecto.Usuario.UsuarioModel.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Verifica si existe un usuario con un email específico
    boolean existsByEmail(String email);

    // Verifica si existe un usuario con un username específico
    boolean existsByUsername(String username);
}
