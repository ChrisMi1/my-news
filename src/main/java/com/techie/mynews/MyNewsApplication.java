package com.techie.mynews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyNewsApplication {
	public static void main(String[] args) {
		SpringApplication.run(MyNewsApplication.class, args);
	}
}
