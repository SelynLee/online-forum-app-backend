package com.beaconfire.history_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.beaconfire.history_service.feign")
@EnableCaching
public class HistoryServiceApplication {
	public static void main(String[] args) {
		System.out.print("HISTORY SERVICE RUNNING XXXXX");
		SpringApplication.run(HistoryServiceApplication.class, args);
	}
}