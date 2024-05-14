package com.final_project_spring;

import static com.final_project_spring.entity.Role.ADMIN;
import static com.final_project_spring.entity.Role.USER;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.final_project_spring.dto.RegisterRequest;
import com.final_project_spring.service.AuthenticationService;


@SpringBootApplication
public class SpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(AuthenticationService service) {
		return args -> {
			var admin = RegisterRequest.builder()
					.firstName("Admin")
					.lastName("Admin")
					.email("admin@mail.com")
					.password("password")
					.role(ADMIN).build();
			System.out.println("This is admin token: " + service.register(admin).getAccessToken());
			
			var user = RegisterRequest.builder()
					.firstName("User")
					.lastName("User")
					.email("user@mail.com")
					.password("password")
					.role(USER).build();
			System.out.println("This is user token: " + service.register(user).getAccessToken());
		};
	}
}
