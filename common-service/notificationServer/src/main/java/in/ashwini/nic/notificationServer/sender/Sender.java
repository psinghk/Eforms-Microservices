package in.ashwini.nic.notificationServer.sender;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.ashwini.nic.notificationServer.config.MailProperties;
import in.ashwini.nic.notificationServer.model.CustomMessage;
import in.ashwini.nic.notificationServer.model.FinalCustomMessage;

@Component
public class Sender {
	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private MailProperties mailProperties;

	public String send(FinalCustomMessage finalCustomMessage) {
		if (finalCustomMessage.getIsOtp() != null && finalCustomMessage.getIsOtp()) {
				if(finalCustomMessage.getFileNamesForAttachment() != null && finalCustomMessage.getFileNamesForAttachment().size() > 0) {
					rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForEmailAttachment(), finalCustomMessage);
					rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForSmsOtp(), finalCustomMessage);
					return "Message sent successfully to attachment Queue!!!";
				}else {
					if(finalCustomMessage.getEmail() != null && !finalCustomMessage.getEmail().isEmpty() && finalCustomMessage.getMobile() != null && !finalCustomMessage.getMobile().isEmpty()) {
						rabbitTemplate.convertAndSend(mailProperties.getOtpFanoutExchange(), "", finalCustomMessage);
					} else if(finalCustomMessage.getMobile() != null && !finalCustomMessage.getMobile().isEmpty()) {
						rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForSmsOtp(), finalCustomMessage);
					} else if(finalCustomMessage.getEmail() != null && !finalCustomMessage.getEmail().isEmpty()) {
						rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForEmailOtp(), finalCustomMessage);
					} else {
						return "Invalid data sent !!!";
					}
					
				}
				return "Message sent successfully to OTP queues!!!";
		}else {
			if(finalCustomMessage.getFileNamesForAttachment() != null && finalCustomMessage.getFileNamesForAttachment().size() > 0) {
				rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForEmailAttachment(), finalCustomMessage);
				rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForSms(), finalCustomMessage);
				return "Message sent successfully to attachment Queue!!!";
			}else {
				if(finalCustomMessage.getEmail() != null && !finalCustomMessage.getEmail().isEmpty() && finalCustomMessage.getMobile() != null && !finalCustomMessage.getMobile().isEmpty()) {
					rabbitTemplate.convertAndSend(mailProperties.getFanoutExchange(), "", finalCustomMessage);
				} else if(finalCustomMessage.getMobile() != null && !finalCustomMessage.getMobile().isEmpty()) {
					rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForSms(), finalCustomMessage);
				} else if(finalCustomMessage.getEmail() != null && !finalCustomMessage.getEmail().isEmpty()) {
					rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForEmail(), finalCustomMessage);
				} else {
					return "Invalid data sent !!!";
				}
			}
			return "Message sent successfully!!!";
		}
	}
	
	
	
	
	public String sendEmailOrSms(CustomMessage message) {
		if (message.getIsOtp() != null && message.getIsOtp()) {
				if(message.getFileNamesForAttachment() != null && message.getFileNamesForAttachment().size() > 0) {
					rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForEmailAttachment(), message);
					rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForSmsOtp(), message);
					return "Message sent successfully to attachment Queue!!!";
				}else {
					if(message.getEmail() != null && !message.getEmail().isEmpty() && message.getMobile() != null && !message.getMobile().isEmpty()) {
						rabbitTemplate.convertAndSend(mailProperties.getOtpFanoutExchange(), "", message);
					} else if(message.getMobile() != null && !message.getMobile().isEmpty()) {
						rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForSmsOtp(), message);
					} else if(message.getEmail() != null && !message.getEmail().isEmpty()) {
						rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForEmailOtp(), message);
					} else {
						return "Invalid data sent !!!";
					}
					
				}
				return "Message sent successfully to OTP queues!!!";
		}else {
			if(message.getFileNamesForAttachment() != null && message.getFileNamesForAttachment().size() > 0) {
				rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForEmailAttachment(), message);
				rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForSms(), message);
				return "Message sent successfully to attachment Queue!!!";
			}else {
				if(message.getEmail() != null && !message.getEmail().isEmpty() && message.getMobile() != null && !message.getMobile().isEmpty()) {
					rabbitTemplate.convertAndSend(mailProperties.getFanoutExchange(), "", message);
				} else if(message.getMobile() != null && !message.getMobile().isEmpty()) {
					 rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForSms(), message);
				} else if(message.getEmail() != null && !message.getEmail().isEmpty()) {
					rabbitTemplate.convertAndSend(mailProperties.getDirectExchange(), mailProperties.getRoutingKeyForEmail(), message);
				} else {
					return "Invalid data sent !!!";
				}
			}
			return "Message sent successfully!!!";
		}
	}
}
