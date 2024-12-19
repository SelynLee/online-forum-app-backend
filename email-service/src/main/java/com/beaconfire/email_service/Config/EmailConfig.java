package com.beaconfire.email_service.Config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@Data
@Getter
@Setter
@RefreshScope
public class EmailConfig {

    @Value("${email.smtp.host}")
    private String smtpHost;

    @Value("${email.smtp.email}")
    private String smtpEmail;

    @Value("${email.smtp.password}")
    private String smtpPassword;

    @Value("${email.smtp.from.address}")
    private String fromEmailAddress;

    @Value("${config.version}")
    private String configVersion;
}
