package com.telcaria.dcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration
		.class })
public class DcsApplication {


	public static void main(String[] args) {
		SpringApplication.run(DcsApplication.class, args);
	}

}
