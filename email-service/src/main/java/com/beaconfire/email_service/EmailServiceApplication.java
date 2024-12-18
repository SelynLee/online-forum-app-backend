package com.beaconfire.email_service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.core.Queue;


import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;




@SpringBootApplication
public class EmailServiceApplication {

	private final String SMTP_HOST = "email-smtp.us-east-2.amazonaws.com";
	private final String SMTP_EMAIL = "AKIAQMCPTSJ7JW7AATNK";
	private final String SMTP_PASSWORD = "BLL3JKaWwNtiEAcHcPicHX2U++kznitWYa3BVRrIRxX7";
	private final String From_EMAIL_ADDRESS = "no-reply@nico-nico-nii.com";
	public static void main(String[] args) {
		SpringApplication.run(EmailServiceApplication.class, args);
//		EmailServiceApplication app = new EmailServiceApplication();
//        try {
//            app.sendEmail("dongyuxiaoned@gmail.com","Neddong");
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
    }


	@Bean
	public Queue emailServiceQueue() {
		return new Queue("email_service_queue", false);
	}


	@RabbitListener(queues = "email_service_queue")
	public void handleEmailMessage(String message) {
		try {
			System.out.println(" [x] Received '" + message + "'");

			// 使用 Jackson ObjectMapper 解析 JSON 字符串
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


//	private void sendEmail(String recipientEmail, String firstname, String lastname, String url) throws MessagingException {
//
//		Properties properties = new Properties();
//		properties.put("mail.smtp.auth", "true");
//		properties.put("mail.smtp.starttls.enable", "true");
//		properties.put("mail.smtp.host", SMTP_HOST);
//		properties.put("mail.smtp.port", "587");
//
//
//		Session session = Session.getInstance(properties, new Authenticator() {
//			@Override
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication(SMTP_EMAIL, SMTP_PASSWORD);
//			}
//		});
//
//
//		Message message = new MimeMessage(session);
//		message.setFrom(new InternetAddress(From_EMAIL_ADDRESS));
//		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
//		message.setSubject("Welcome to Our Service");
//		message.setText("Dear " + firstname+ " "+lastname + ",\n\nThank you for registering with us!, your token is: \n" + url);
//		message.setText("Dear " + firstname + " " + lastname + ",\n\n"
//						+ "Thank you for registering with us! Your token is:\n"
//						+ "<a href='" + url + "'>" + url + "</a>",
//				"UTF-8", "html");
//
//
//
//		Transport.send(message);
//		System.out.println("Email sent successfully to: " + recipientEmail);
//	}
	private void sendEmail(String recipientEmail, String firstname, String lastname, String url) throws MessagingException {

	Properties properties = new Properties();
	properties.put("mail.smtp.auth", "true");
	properties.put("mail.smtp.starttls.enable", "true");
	properties.put("mail.smtp.host", SMTP_HOST);
	properties.put("mail.smtp.port", "587");

	Session session = Session.getInstance(properties, new Authenticator() {
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(SMTP_EMAIL, SMTP_PASSWORD);
		}
	});

	Message message = new MimeMessage(session);
	message.setFrom(new InternetAddress(From_EMAIL_ADDRESS));
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
