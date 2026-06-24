package cl.duoc.login.ServiceTest;

import cl.duoc.login.dto.request.DtoAuthRequest;
import cl.duoc.login.dto.response.DtoAuthResponse;
import cl.duoc.login.model.UsuarioModel;
import cl.duoc.login.repository.UsuarioRepository;
import cl.duoc.login.service.AuthService;
import cl.duoc.login.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private UsuarioModel usuario;

    @BeforeEach
    void setUp() {
        usuario = new UsuarioModel(1L, "admin", "1234", "ADMIN", true);
    }

    @Test
    void loginExitosoDevuelveToken() {
        // Given: usuario existe, password correcto y JwtService genera token
        DtoAuthRequest request = new DtoAuthRequest("admin", "1234");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(jwtService.generarToken("admin", "ADMIN")).thenReturn("token-jwt-123");

        // When: se hace login
        DtoAuthResponse resultado = authService.login(request);

        // Then: la respuesta contiene el token JWT
        assertThat(resultado.getToken()).isEqualTo("token-jwt-123");
        verify(jwtService).generarToken("admin", "ADMIN");
    }

    @Test
    void loginUsuarioNoExisteLanzaExcepcion() {
        // Given: el username no existe en la base de datos
        DtoAuthRequest request = new DtoAuthRequest("desconocido", "1234");
        when(usuarioRepository.findByUsername("desconocido")).thenReturn(Optional.empty());

        // When / Then: debe lanzar excepción de credenciales incorrectas
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario o password incorrecto");
    }

    @Test
    void loginPasswordIncorrectoLanzaExcepcion() {
        // Given: usuario existe pero la contraseña no coincide
        DtoAuthRequest request = new DtoAuthRequest("admin", "mal");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        // When / Then: debe lanzar excepción de credenciales incorrectas
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario o password incorrecto");
    }

    @Test
    void loginUsuarioDeshabilitadoLanzaExcepcion() {
        // Given: usuario existe pero está deshabilitado
        usuario.setEnabled(false);
        DtoAuthRequest request = new DtoAuthRequest("admin", "1234");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        // When / Then: debe lanzar excepción de usuario deshabilitado
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario deshabilitado");
    }

    @Test
    void loginPasswordCorrectoGeneraTokenConRol() {
        // Given: credenciales válidas y JwtService listo para generar token
        DtoAuthRequest request = new DtoAuthRequest("admin", "1234");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(jwtService.generarToken("admin", "ADMIN")).thenReturn("jwt-admin");

        // When: se hace login
        authService.login(request);

        // Then: JwtService genera token con username y rol
        verify(jwtService).generarToken("admin", "ADMIN");
    }
}
