package com.metuncc.mlm;

import com.metuncc.mlm.exception.ExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ExceptionHandler.class})
public class MlmApplication {

	public static void main(String[] args) {
		SpringApplication.run(MlmApplication.class, args);
	}

}
