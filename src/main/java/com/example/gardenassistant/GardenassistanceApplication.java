package com.example.gardenassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching(proxyTargetClass = true)
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class GardenassistanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GardenassistanceApplication.class, args);
	}

}
