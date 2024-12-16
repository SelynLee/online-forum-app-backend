package com.beaconfire.users_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;


@SpringBootApplication
public class UsersServiceApplication {

	public static void main(String[] args) {
		System.out.print("USER SERVICE RUNNING XXXXX");
		SpringApplication.run(UsersServiceApplication.class, args);
	}

}
