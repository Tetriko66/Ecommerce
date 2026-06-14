package cl.duoc.login.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// Indica que esta clase es una configuración de Spring
@Configuration
// Indica que leerá propiedades que comiencen con jwt
@ConfigurationProperties(prefix = "jwt")

public class JwtProperties {

    // Guarda el valor de jwt.secret
    private String secret;
    // Guarda el valor de jwt.expiration
    private Long expiration;
}
