package in.ashwini.nic.notificationServer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "mail")
@Data
public class MailProperties {
        String emailQueue;
        String smsQueue;
        String emailOtpQueue;
        String emailQueueWithAttachment;
        String smsOtpQueue;
        String fanoutExchange;
        String otpFanoutExchange;
        String directExchange;
        Boolean isMailEnabled;
        Boolean isSmsEnabled;
        String routingKeyForEmailAttachment;
        String routingKeyForSms;
        String host;
        Integer port;
        String username;
        String password;
}
