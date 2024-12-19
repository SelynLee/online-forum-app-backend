package com.beaconfire.posts_service.config;


import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Check multiple aspects of the application's health
        boolean databaseStatus = checkDatabaseStatus();
        boolean externalServiceStatus = checkExternalServiceStatus("https://example.com/health");

        if (databaseStatus && externalServiceStatus) {
            return Health.up()
                    .withDetail("Server Status", "All Systems Operational")
                    .withDetail("Database Status", "Connected")
                    .withDetail("External Service", "Available")
                    .build();
        } else {
            return Health.down()
                    .withDetail("Server Status", "Degraded")
                    .withDetail("Database Status", databaseStatus ? "Connected" : "Disconnected")
                    .withDetail("External Service", externalServiceStatus ? "Available" : "Unavailable")
                    .build();
        }
    }

    /**
     * Simulate a database health check.
     * Replace this with actual database connection health logic.
     */
    private boolean checkDatabaseStatus() {
        // Example: Check a database connection
        // Add actual database health logic (e.g., execute a lightweight query)
        return true; // Assume database is always up for now
    }

    /**
     * Check the health of an external service by sending a ping request.
     */
    private boolean checkExternalServiceStatus(String serviceUrl) {
        try {
            URL url = new URL(serviceUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(2000); // 2 seconds timeout
            connection.connect();
            return (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            return false; // Service is unavailable
        }
    }
}

