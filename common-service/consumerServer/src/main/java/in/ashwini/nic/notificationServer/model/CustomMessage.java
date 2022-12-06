package in.ashwini.nic.notificationServer.model;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CustomMessage {
	private String email;
	private String mobile;
	private String emailContent;
	private String smsContent;
	private String subject;
	private String from;
	private List<String> cc;
	private List<String> fileNamesForAttachment;
	//private List<MultipartFile> attachmentsFiles;
	private Map<String, byte[]> attachments;
	private String templateId;
	private Boolean isOtp;	
}
