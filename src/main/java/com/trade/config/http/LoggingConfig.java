package com.trade.config.http;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gaodp
 */
@Configuration
public class LoggingConfig {

    @Bean
    @ConfigurationProperties(prefix = "logging.request")
    public RequestLoggingProperties requestLoggingProperties() {
        return new RequestLoggingProperties();
    }
}
