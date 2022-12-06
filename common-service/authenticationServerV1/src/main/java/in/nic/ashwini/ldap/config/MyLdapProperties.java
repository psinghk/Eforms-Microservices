package in.nic.ashwini.ldap.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "ldap")
//@PropertySource("classpath:application.properties")
//@PropertySource("file:/Users/ashwini/config/ldap.properties")
@Data
public class MyLdapProperties {
	String adminId;
	String baseDN;
	String adminPassword;
	int port;
	String url;
}
