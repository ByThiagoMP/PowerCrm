package com.service.powercrm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@EntityScan(basePackages = "com.service.powercrm.domain")
@SpringBootApplication
public class PowerCrmApplication {

	public static void main(String[] args) {
		SpringApplication.run(PowerCrmApplication.class, args);
	}

}
