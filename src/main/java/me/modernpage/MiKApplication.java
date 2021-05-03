package me.modernpage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAutoConfiguration(exclude = HypermediaAutoConfiguration.class)
@EnableJpaAuditing
@EnableAspectJAutoProxy
@EnableAsync
public class MiKApplication {
	public static void main(String[] args) {
		SpringApplication.run(MiKApplication.class, args);
	}
}