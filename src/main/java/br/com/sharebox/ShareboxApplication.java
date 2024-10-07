package br.com.sharebox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class ShareboxApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShareboxApplication.class, args);
	}

}
