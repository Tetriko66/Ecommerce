package com.projecto.Usuario.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.projecto.Usuario.UsuarioDTO.Request.Request;
import com.projecto.Usuario.UsuarioDTO.Response.Response;
import com.projecto.Usuario.UsuarioModel.Usuario;
import com.projecto.Usuario.UsuarioRepository.UsuarioRepository;
import com.projecto.Usuario.UsuarioService.UsuarioService;


@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    
    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
     usuario = new Usuario(1L,"usuario1", "contrasena1", "correo1@example.com");
    }

    @Test
    void crearUsuarioyDevuelveUsuarioCreado(){
        Request request = new Request("usuario1", "contrasena1", "correo1@example.com");
        when(repository.existsByEmail("correo1@example.com"))
            .thenReturn(false);
        
        when(repository.existsByUsername("usuario1"))
            .thenReturn(false);

        when(repository.save(any(Usuario.class)))
            .thenAnswer(invocation ->{ 
                Usuario u = invocation.getArgument(0);
                u.setId(1L);
                return u;
            });
        
        Response resultado = service.crearUsuario(request);

        assertThat(resultado.getUsername()).isEqualTo("usuario1");
        assertThat(resultado.getId()).isEqualTo(1L);
    
        }
}
