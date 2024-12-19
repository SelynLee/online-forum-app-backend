package com.beaconfire.email_service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

@SpringBootApplication
public class EmailServiceApplication {

	@Value("${email.smtp.host}")
	private String smtpHost;

	@Value("${email.smtp.email}")
	private String smtpEmail;

	@Value("${email.smtp.password}")
	private String smtpPassword;

	@Value("${email.smtp.from.address}")
	private String fromEmailAddress;

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

			sendEmail(email, firstname, lastname, url);

		} catch (Exception e) {
			System.err.println("Error handling message: " + e.getMessage());
		}
	}

	private void sendEmail(String recipientEmail, String firstname, String lastname, String url) throws MessagingException {
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", smtpHost);
		properties.put("mail.smtp.port", "587");

		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(smtpEmail, smtpPassword);
			}
		});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(fromEmailAddress));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
		message.setSubject("Welcome to Our Service");

		String htmlContent = "<html>" +
				"<body>" +
				"<p>Dear " + firstname + " " + lastname + ",</p>" +
				"<p>Thank you for registering with us! Your token is:</p>" +
				"<p><a href='" + url + "'>" + url + "</a></p>" +
				"</body>" +
				"</html>";

		message.setContent(htmlContent, "text/html; charset=UTF-8");

		Transport.send(message);
		System.out.println("Email sent successfully to: " + recipientEmail);
	}
}
