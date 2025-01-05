package com.rauldev.literAlura;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import com.rauldev.literAlura.principal.Principal;
import com.rauldev.literAlura.service.ApiService;
import com.rauldev.literAlura.service.ConvierteDatos;
import com.rauldev.literAlura.service.LibroService;

@SpringBootApplication
public class LiterAluraApplication implements CommandLineRunner {
    private final Principal principal;
    // Constructor que inyecta `Principal` con `@Lazy`
	public LiterAluraApplication(@Lazy Principal principal) {
		this.principal = principal;
	}

    public static void main(String[] args) {
        SpringApplication.run(LiterAluraApplication.class, args);

    }
    @Override
	public void run(String... args) throws Exception {
		principal.iniciar();
	}
    @Bean
	public ApiService apiService() {
		return new ApiService();
	}

	@Bean
	public ConvierteDatos conversor() {
		return new ConvierteDatos();
	}
	@Bean
	public LibroService librosService() {
		return new LibroService();
	}	

	public Principal getPrincipal() {
		return principal;
	}
}
