package com.metuncc.mlm;

import com.metuncc.mlm.exception.ExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@Import({ExceptionHandler.class})
@EnableWebMvc
@EnableScheduling
@EnableSwagger2
public class MlmApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(MlmApplication.class, args);
	}

}
