package com.beaconfire.auth_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.rabbitmq.client.ConnectionFactory;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.connection.host}")
    private String host;

    @Value("${rabbitmq.connection.port}")
    private int port;

    @Value("${rabbitmq.connection.username}")
    private String username;

    @Value("${rabbitmq.connection.password}")
    private String password;

    @Value("${rabbitmq.connection.ssl.enabled}")
    private boolean sslEnabled;

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);

        if (sslEnabled) {
            try {
                factory.useSslProtocol();
            } catch (Exception e) {
                throw new RuntimeException("Failed to configure SSL for RabbitMQ", e);
            }
        }
        return factory;
    }
}