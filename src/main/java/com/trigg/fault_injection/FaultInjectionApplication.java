package com.trigg.fault_injection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FaultInjectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(FaultInjectionApplication.class, args);
	}

}
