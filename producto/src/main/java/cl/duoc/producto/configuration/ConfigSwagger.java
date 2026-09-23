package cl.duoc.producto.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigSwagger {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(
            new Info()
                .title("Microservicio Producto")
                .version("1.0")
                .description("Documentación de la API de Productos del Ecommerce")
        );
    }
}
