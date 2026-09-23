package cl.duoc.inventario.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Bean de WebClient disponible para inyectar en los servicios
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
