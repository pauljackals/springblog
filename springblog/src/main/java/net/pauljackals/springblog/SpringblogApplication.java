package net.pauljackals.springblog;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.ImportResource;

import net.pauljackals.springblog.service.storage.StorageProperties;
import net.pauljackals.springblog.service.storage.StorageService;

@SpringBootApplication
// @ImportResource("classpath*:beans.xml")
@EnableConfigurationProperties(StorageProperties.class)
public class SpringblogApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringblogApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.init();
		};
	}
}
