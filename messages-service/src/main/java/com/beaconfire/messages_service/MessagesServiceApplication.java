package com.beaconfire.messages_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MessagesServiceApplication {

	public static void main(String[] args) {
		System.out.print("MEASSSAGE SERVICE RUNNING XXXXX");
		SpringApplication.run(MessagesServiceApplication.class, args);
	}

}
