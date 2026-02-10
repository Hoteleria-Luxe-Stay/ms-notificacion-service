package com.hotel.notificacion.infrastructure.config;

import com.klab.email.config.EmailConfiguration;
import com.klab.email.config.EmailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(EmailConfiguration.class)
@EnableConfigurationProperties(EmailProperties.class)
public class EmailLibConfig {
}
