package in.ashwini.nic.notificationServer.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {
	@Autowired
	private MailProperties mailProperties;

	@Bean
	public Declarables fanoutBindings() {
		Queue emailQueue = new Queue(mailProperties.getEmailQueue());
		Queue smsQueue = new Queue(mailProperties.getSmsQueue());
		Queue emailOtpQueue = new Queue(mailProperties.getEmailOtpQueue());
		Queue smsOtpQueue = new Queue(mailProperties.getSmsOtpQueue());
		Queue emailQueueWithAttachment = new Queue(mailProperties.getEmailQueueWithAttachment());
		FanoutExchange fanoutExchange = new FanoutExchange(mailProperties.getFanoutExchange());
		FanoutExchange otpFanoutExchange = new FanoutExchange(mailProperties.getOtpFanoutExchange());
		DirectExchange directExchange = new DirectExchange(mailProperties.getDirectExchange());

		return new Declarables(emailQueue, smsQueue, emailOtpQueue, smsOtpQueue, emailQueueWithAttachment,
				fanoutExchange, otpFanoutExchange, directExchange, BindingBuilder.bind(emailQueue).to(fanoutExchange),
				BindingBuilder.bind(smsQueue).to(fanoutExchange),
				BindingBuilder.bind(smsOtpQueue).to(otpFanoutExchange),
				BindingBuilder.bind(emailOtpQueue).to(otpFanoutExchange),
				BindingBuilder.bind(smsQueue).to(directExchange).with(mailProperties.getRoutingKeyForSms()),
				BindingBuilder.bind(emailQueueWithAttachment).to(directExchange).with(mailProperties.getRoutingKeyForEmailAttachment()));
	}

	@Bean
	public AmqpTemplate template(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}
	
	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
