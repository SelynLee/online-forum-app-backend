package com.beaconfire.posts_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PostsServiceApplication {

	public static void main(String[] args) {
		System.out.print("POST SERVICE RUNNING XXXXX");
		SpringApplication.run(PostsServiceApplication.class, args);
	}

}
