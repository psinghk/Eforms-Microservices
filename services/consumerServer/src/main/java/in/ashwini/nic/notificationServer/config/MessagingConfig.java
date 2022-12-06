package in.ashwini.nic.notificationServer.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import in.ashwini.nic.notificationServer.receiver.EmailOtpReceiver;
import in.ashwini.nic.notificationServer.receiver.EmailReceiver;
import in.ashwini.nic.notificationServer.receiver.EmailWithAttachmentReceiver;
import in.ashwini.nic.notificationServer.receiver.SmsOtpReceiver;
import in.ashwini.nic.notificationServer.receiver.SmsReceiver;

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

	@Bean
	SimpleMessageListenerContainer container1(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter1) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(mailProperties.getEmailQueue());
		container.setMessageListener(listenerAdapter1);
		return container;
	}

	@Bean
	SimpleMessageListenerContainer container2(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter2) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(mailProperties.getSmsQueue());
		container.setMessageListener(listenerAdapter2);
		return container;
	}

	@Bean
	SimpleMessageListenerContainer container3(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter3) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(mailProperties.getEmailOtpQueue());
		container.setMessageListener(listenerAdapter3);
		return container;
	}

	@Bean
	SimpleMessageListenerContainer container4(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter4) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(mailProperties.getSmsOtpQueue());
		container.setMessageListener(listenerAdapter4);
		return container;
	}

	@Bean
	SimpleMessageListenerContainer container5(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter5) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(mailProperties.getEmailQueueWithAttachment());
		container.setMessageListener(listenerAdapter5);
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter1(EmailReceiver receiver) {
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver, "receiveMessage");
		messageListenerAdapter.setMessageConverter(jsonMessageConverter());
		return messageListenerAdapter;
	}

	@Bean
	MessageListenerAdapter listenerAdapter2(SmsReceiver receiver) {
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver, "receiveMessage");
		messageListenerAdapter.setMessageConverter(jsonMessageConverter());
		return messageListenerAdapter;
	}

	@Bean
	MessageListenerAdapter listenerAdapter3(EmailOtpReceiver receiver) {
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver, "receiveMessage");
		messageListenerAdapter.setMessageConverter(jsonMessageConverter());
		return messageListenerAdapter;
	}

	@Bean
	MessageListenerAdapter listenerAdapter4(SmsOtpReceiver receiver) {
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver, "receiveMessage");
		messageListenerAdapter.setMessageConverter(jsonMessageConverter());
		return messageListenerAdapter;
	}

	@Bean
	MessageListenerAdapter listenerAdapter5(EmailWithAttachmentReceiver receiver) {
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver, "receiveMessage");
		messageListenerAdapter.setMessageConverter(jsonMessageConverter());
		return messageListenerAdapter;
	}
	
	@Bean
	JavaMailSender javaMailSender() {
		JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
		javaMailSenderImpl.setHost(mailProperties.getHost());
		javaMailSenderImpl.setPort(mailProperties.getPort());
		javaMailSenderImpl.setUsername(mailProperties.getUsername());
		javaMailSenderImpl.setPassword(mailProperties.getPassword());
		javaMailSenderImpl.setProtocol("smtp");
		return javaMailSenderImpl;
	}
}
