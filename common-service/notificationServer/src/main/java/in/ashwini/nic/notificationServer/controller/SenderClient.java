package in.ashwini.nic.notificationServer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.ashwini.nic.notificationServer.model.CustomMessage;
import in.ashwini.nic.notificationServer.model.FinalCustomMessage;
import in.ashwini.nic.notificationServer.sender.Sender;

@RestController
public class SenderClient {
	
	@Autowired
	private Sender sender;
	
	@PostMapping("/send")
	public String send(@ModelAttribute("message") CustomMessage message) throws IOException{
		System.out.println("Sending message...");
		List<String> fileNames = new ArrayList<>();
		Map<String, byte[]> files = new HashMap<>();
		for (MultipartFile singleFile : message.getAttachmentsFiles()) {
			fileNames.add(singleFile.getOriginalFilename());
			files.put(singleFile.getOriginalFilename(), singleFile.getBytes());
		}
		message.setFileNamesForAttachment(fileNames);
		message.setAttachments(files);
		FinalCustomMessage finalCustomMessage = new FinalCustomMessage();
		finalCustomMessage.setAttachments(message.getAttachments() !=null ? message.getAttachments() : null);
		finalCustomMessage.setCc(message.getCc()!=null ? message.getCc() : null);
		finalCustomMessage.setEmail(message.getEmail() !=null ? message.getEmail() : null);
		finalCustomMessage.setEmailContent(message.getEmailContent() !=null ? message.getEmailContent() : null);
		finalCustomMessage.setFileNamesForAttachment(message.getFileNamesForAttachment() !=null ? message.getFileNamesForAttachment() : null);
		finalCustomMessage.setFrom(message.getFrom() !=null ? message.getFrom() : null);
		finalCustomMessage.setIsOtp(message.getIsOtp() !=null ? message.getIsOtp() : null);
		finalCustomMessage.setMobile(message.getMobile() !=null ? message.getMobile() : null);
		finalCustomMessage.setSmsContent(message.getSmsContent() !=null ? message.getSmsContent() : null);
		finalCustomMessage.setSubject(message.getSubject() !=null ? message.getSubject() : null);
		finalCustomMessage.setTemplateId(message.getTemplateId() !=null ? message.getTemplateId() : null);
		sender.send(finalCustomMessage);
		return "Message successfully sent!!!";
	}
	

	@PostMapping("/sendThroughMultiPart")
	public String sendThroughMultiPart(@ModelAttribute("message") CustomMessage message) throws IOException {
		System.out.println("Sending message...");
		List<String> fileNames = new ArrayList<>();
		Map<String, byte[]> files = new HashMap<>();
//		for (MultipartFile singleFile : message.getAttachmentsFiles()) {
//			fileNames.add(singleFile.getOriginalFilename());
//			files.put(singleFile.getOriginalFilename(), singleFile.getBytes());
//		}
		message.setFileNamesForAttachment(fileNames);
		message.setAttachments(files);
		FinalCustomMessage finalCustomMessage = new FinalCustomMessage();
		finalCustomMessage.setAttachments(message.getAttachments() != null ? message.getAttachments() : null);
		finalCustomMessage.setCc(message.getCc() != null ? message.getCc() : null);
		finalCustomMessage.setEmail(message.getEmail() != null ? message.getEmail() : null);
		finalCustomMessage.setEmailContent(message.getEmailContent() != null ? message.getEmailContent() : null);
		finalCustomMessage.setFileNamesForAttachment(
				message.getFileNamesForAttachment() != null ? message.getFileNamesForAttachment() : null);
		finalCustomMessage.setFrom(message.getFrom() != null ? message.getFrom() : null);
		finalCustomMessage.setIsOtp(message.getIsOtp() != null ? message.getIsOtp() : null);
		finalCustomMessage.setMobile(message.getMobile() != null ? message.getMobile() : null);
		finalCustomMessage.setSmsContent(message.getSmsContent() != null ? message.getSmsContent() : null);
		finalCustomMessage.setSubject(message.getSubject() != null ? message.getSubject() : null);
		finalCustomMessage.setTemplateId(message.getTemplateId() != null ? message.getTemplateId() : null);
		// sender.send(finalCustomMessage);
		return "Message successfully sent!!!";
	}
	
	
	@PostMapping("/sendEmailOrSms")
	public String sendEmailOrSms(@RequestBody @Valid CustomMessage message) {
		System.out.println("Sending message...");
		try {
			sender.sendEmailOrSms(message);
		} catch (Exception ex) {
			return "Could not sent Message";
		}
		return "Message sent successfully!!!!";
	}
}
