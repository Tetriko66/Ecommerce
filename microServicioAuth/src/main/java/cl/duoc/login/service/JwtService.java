package cl.duoc.login.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import cl.duoc.login.configuration.JwtProperties;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.Date;

// Indica que esta clase es un servicio de Spring
@Service

// Genera constructor con los atributos final
@RequiredArgsConstructor
public class JwtService {

    // Inyecta las propiedades jwt.secret y jwt.expiration
    private final JwtProperties jwtProperties;

    // Método que genera un token usando username y role
    public String generarToken(String username, String role) {

        // Crea y retorna un JWT firmado
        return JWT.create()

                // Define el usuario dueño del token
                .withSubject(username)

                // Define quién emitió el token
                .withIssuer("login-service")

                // Agrega el rol como claim personalizado
                .withClaim("role", role)

                // Agrega la fecha de creación del token
                .withIssuedAt(new Date())

                // Agrega la fecha de expiración del token
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))

                // Firma el token usando algoritmo HMAC256 y la clave secreta
                .sign(Algorithm.HMAC256(jwtProperties.getSecret()));
    }
}
