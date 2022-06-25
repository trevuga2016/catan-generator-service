package com.generatecatanboard.catangeneratorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CatanGeneratorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatanGeneratorServiceApplication.class, args);
	}

}
