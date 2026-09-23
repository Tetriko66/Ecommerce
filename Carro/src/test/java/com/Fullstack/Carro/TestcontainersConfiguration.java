package com.Fullstack.Carro;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    MySQLContainer<?> mysqlContainer() {                          // CORREGIDO: wildcard genérico
        return new MySQLContainer<>(DockerImageName.parse("mysql:8.4")); // CORREGIDO: versión fija
    }

}
