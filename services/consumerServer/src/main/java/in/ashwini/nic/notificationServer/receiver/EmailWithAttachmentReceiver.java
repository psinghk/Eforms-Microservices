package in.ashwini.nic.notificationServer.receiver;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import in.ashwini.nic.notificationServer.config.MailProperties;
import in.ashwini.nic.notificationServer.model.FinalCustomMessage;

@Component
public class EmailWithAttachmentReceiver {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	Configuration configuration;

	@Autowired
	private MailProperties mailProperties;

	public void receiveMessage(FinalCustomMessage message) throws MessagingException, IOException, TemplateException {
		System.out.println("Received by Email with attachment consumer <" + message.toString() + ">");

		if (mailProperties.getIsMailEnabled()) {

			MimeMessage msg = javaMailSender.createMimeMessage();

			// true = multipart message
			MimeMessageHelper helper;

			helper = new MimeMessageHelper(msg, true);

			helper.setTo(message.getEmail());

			helper.setSubject(message.getSubject());

			// default = text/plain
			// helper.setText("Check attachment for image!");

			// true = text/html
			helper.setText("<h1>Check attachment for image!</h1>", true);

			// hard coded a file path
			// FileSystemResource file = new FileSystemResource(new
			// File("path/android.png"));
			Map<String, byte[]> attachments = message.getAttachments();

			List<String> fileNames = message.getFileNamesForAttachment();

			for (String fileName : fileNames) {
				final InputStreamSource fileStreamSource = new ByteArrayResource(attachments.get(fileName));
				helper.addAttachment(fileName, fileStreamSource);
			}

			javaMailSender.send(msg);
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