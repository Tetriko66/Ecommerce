package cl.duoc.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

// Este microservicio es el registro central de servicios.
// Auth, Fiesta y Gateway se registran aquí al iniciar.
@SpringBootApplication
@EnableEurekaServer // Esto convierte este proyecto Spring Boot en un servidor Eureka.
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
