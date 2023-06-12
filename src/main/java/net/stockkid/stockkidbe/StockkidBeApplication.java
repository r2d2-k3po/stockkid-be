package net.stockkid.stockkidbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StockkidBeApplication {
	public static void main(String[] args) {
		SpringApplication.run(StockkidBeApplication.class, args);
	}

}
