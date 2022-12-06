package in.ashwini.nic.notificationServer.receiver;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import in.ashwini.nic.notificationServer.config.MailProperties;
import in.ashwini.nic.notificationServer.model.FinalCustomMessage;

@Component
public class EmailOtpReceiver {

	@Autowired
	Configuration configuration;

	@Autowired
	private MailProperties mailProperties;

	@Autowired
	private JavaMailSender javaMailSender;

	public void receiveMessage(FinalCustomMessage message) throws MessagingException, IOException, TemplateException {
		System.out.println("Received by Email OTP consumer <" + message.toString() + ">");
		if (mailProperties.getIsMailEnabled()) {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
			helper.setSubject(message.getSubject());
			helper.setTo(message.getEmail());
			String emailContent = getEmailContent(message);
			helper.setText(emailContent, true);
			helper.setFrom("ashwin@gov.in");
			javaMailSender.send(mimeMessage);
		}
	}

	private String getEmailContent(FinalCustomMessage message) throws IOException, TemplateException {
		StringWriter stringWriter = new StringWriter();
		Map<String, Object> model = new HashMap<>();
		model.put("message", message);
		configuration.getTemplate("email.ftlh").process(model, stringWriter);
		return stringWriter.getBuffer().toString();
	}
}