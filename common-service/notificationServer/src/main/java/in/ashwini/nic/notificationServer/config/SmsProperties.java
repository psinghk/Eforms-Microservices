package in.ashwini.nic.notificationServer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "sms")
@Data
public class SmsProperties {
        String ip;
        String usernameForOtp;
        String username;
        String passwordForOtp;
        String password;
}
