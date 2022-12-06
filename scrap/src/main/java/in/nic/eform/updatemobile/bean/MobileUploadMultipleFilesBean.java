package in.nic.eform.updatemobile.bean;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
@Data	
public class MobileUploadMultipleFilesBean {
	@NotEmpty
	private List<MultipartFile> infile;
	@Pattern(regexp = "[A-Z]+-[A-Z]+[0-9]+" ,message ="Wrong Format")
	private String registrationno;
	@NotEmpty
	private String role;
	private String doc;
	private String docpath;
	private String status;
	public List<MultipartFile> getInfile() {
		return infile;
	}
	
	
}
