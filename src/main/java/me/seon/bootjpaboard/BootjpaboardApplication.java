package me.seon.bootjpaboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BootjpaboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootjpaboardApplication.class, args);
	}

}
