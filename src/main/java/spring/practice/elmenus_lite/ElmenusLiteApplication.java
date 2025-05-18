package spring.practice.elmenus_lite;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ElmenusLiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElmenusLiteApplication.class, args);
	}
	@PostConstruct
	public void method() {

	}
}
