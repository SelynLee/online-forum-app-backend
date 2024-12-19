package com.beaconfire.email_service;

import com.beaconfire.email_service.Service.EmailService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EmailServiceApplication {

	@Autowired
	private EmailService emailService;

	public static void main(String[] args) {
		SpringApplication.run(EmailServiceApplication.class, args);
	}

	@Bean
	public Queue emailServiceQueue() {
		return new Queue("email_service_queue", false);
	}

	@RabbitListener(queues = "email_service_queue")
	public void handleEmailMessage(String message) {
		try {
			System.out.println(" [x] Received '" + message + "'");

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(message);

			String email = jsonNode.get("email").asText();
			String firstname = jsonNode.get("firstName").asText();
			String lastname = jsonNode.get("lastName").asText();
			String url = jsonNode.get("url").asText();

			emailService.sendEmail(email, firstname, lastname, url);

		} catch (Exception e) {
			System.err.println("Error handling message: " + e.getMessage());
		}
	}
}
