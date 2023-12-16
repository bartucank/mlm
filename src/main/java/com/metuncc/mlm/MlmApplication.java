package com.metuncc.mlm;

import com.metuncc.mlm.exception.ExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Import({ExceptionHandler.class})
@EnableScheduling
public class MlmApplication {

	public static void main(String[] args) {
		SpringApplication.run(MlmApplication.class, args);
	}

}
