package com.Fullstack.Carro;

import org.springframework.boot.SpringApplication;

public class TestCarroApplication {

	public static void main(String[] args) {
		SpringApplication.from(CarroApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
