package me.modernpage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableAutoConfiguration(exclude = HypermediaAutoConfiguration.class)
@EnableJpaAuditing
@EnableAspectJAutoProxy
public class MiKApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiKApplication.class, args);
	}
}