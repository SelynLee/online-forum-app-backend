package com.beaconfire.eureka_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EurekaServerApplication {

	public static void main(String[] args) {
		System.out.print("EUREKA SERVER RUNNING XXXXX");
		SpringApplication.run(EurekaServerApplication.class, args);
	}

}
