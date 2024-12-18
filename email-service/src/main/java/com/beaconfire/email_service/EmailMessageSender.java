package com.beaconfire.email_service;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

public class EmailMessageSender {

    private static final String QUEUE_NAME = "email_service_queue";
    private static final String RABBITMQ_HOST = "b-420b96b0-7ce2-4393-9684-0ede382d6959.mq.us-east-2.amazonaws.com";
    private static final int RABBITMQ_PORT = 5671;
    private static final String RABBITMQ_USERNAME = "groupproject";
    private static final String RABBITMQ_PASSWORD = "groupproject";

    public void sendEmailMessage(String email, String firstName, String lastName, String url) {

        int token = (int) (Math.random() * 900000) + 100000;
        String message = String.format("{\"email\": \"%s\", \"firstName\": \"%s\", \"lastName\": \"%s\",\"url\": \"%s\"}", email, firstName, lastName,url );


        try {

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(RABBITMQ_HOST);
            factory.setPort(RABBITMQ_PORT);
            factory.setUsername(RABBITMQ_USERNAME);
            factory.setPassword(RABBITMQ_PASSWORD);
            factory.useSslProtocol(); // 启用 SSL


            try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {

                channel.queueDeclare(QUEUE_NAME, false, false, false, null);


                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                System.out.println("Message sent to " + QUEUE_NAME + ": " + message);
            }
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EmailMessageSender sender = new EmailMessageSender();
        sender.sendEmailMessage("dongyuxiaoned@gmail.com", "Gary", "Lin", "http://localhost:8080/auth/validate?token=O3FyKs-ezxjJbqdtFHx_y-jOTQmR6ISuDeQVpp_M8b0");
    }
}
