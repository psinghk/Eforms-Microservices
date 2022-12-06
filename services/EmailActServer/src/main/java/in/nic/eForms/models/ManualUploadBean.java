package in.nic.eForms.models;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class ManualUploadBean {

	private String regNumber;
	private String clientIp;
	private String email;
	private String remarks;
	private MultipartFile infile;
	
	public MultipartFile getInfile()
	{
		return infile;
	}
}
