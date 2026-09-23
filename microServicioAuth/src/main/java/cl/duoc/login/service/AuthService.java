package cl.duoc.login.service;

import org.springframework.stereotype.Service;

import cl.duoc.login.dto.request.DtoAuthRequest;
import cl.duoc.login.dto.response.DtoAuthResponse;
import cl.duoc.login.model.UsuarioModel;
import cl.duoc.login.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// Indica que esta clase es un servicio de Spring
@Service

// Genera constructor con los atributos final
@RequiredArgsConstructor
public class AuthService {

    // Inyecta el repositorio de usuarios
    private final UsuarioRepository usuarioRepository;

    // Inyecta el servicio que genera JWT
    private final JwtService jwtService;

    // Método que valida usuario y contraseña
    public DtoAuthResponse login(DtoAuthRequest request) {

        // Busca el usuario por username en la base de datos
        UsuarioModel usuario = usuarioRepository.findByUsername(request.getUsername())

                // Si no existe, lanza error 401
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario o password incorrecto"));

        // Valida si el usuario está deshabilitado
        if (!usuario.getEnabled()) {

            // Si está deshabilitado, lanza error 401
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario deshabilitado");
        }

        // Compara la contraseña recibida con la guardada en BD
        if (!usuario.getPassword().equals(request.getPassword())) {

            // Si no coincide, lanza error 401
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario o password incorrecto");
        }

        // Genera el token JWT con username y role
        String token = jwtService.generarToken(
                usuario.getUsername(),
                usuario.getRole());

        // Devuelve el token al cliente
        return new DtoAuthResponse(token);
    }
}
