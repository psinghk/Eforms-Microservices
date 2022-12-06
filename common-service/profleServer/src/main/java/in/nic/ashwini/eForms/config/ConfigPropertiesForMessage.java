package in.nic.ashwini.eForms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "message")
@Data
public class ConfigPropertiesForMessage {
	private String somethingwentwrong;
	private String servicedown;
}


