package com.beaconfire.auth_service.service;

import com.beaconfire.auth_service.dto.EmailRequest;
import com.beaconfire.auth_service.exception.RabbitMQException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    private final ConnectionFactory connectionFactory;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    public RabbitMQProducer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.objectMapper = new ObjectMapper();
    }

    public void sendMessage(EmailRequest emailRequest) {
        try {
            String message = objectMapper.writeValueAsString(emailRequest);

            try (Connection connection = connectionFactory.newConnection();
                 Channel channel = connection.createChannel()) {

                channel.queueDeclare(queueName, false, false, false, null);

                channel.basicPublish("", queueName, null, message.getBytes());
            }
        } catch (Exception e) {
            throw new RabbitMQException("Failed to send message to RabbitMQ: " + e.getMessage());
        }
    }
}