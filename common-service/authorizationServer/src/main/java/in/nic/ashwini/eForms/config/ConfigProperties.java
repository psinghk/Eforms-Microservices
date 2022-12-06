package in.nic.ashwini.eForms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "url")
@Data
public class ConfigProperties {
	private String adminUrl;
	private String ldapUrl;
	private String profileUrl;
	private String coordUrl;
	private String hogUrl;
	private String reportingUrl;
}


