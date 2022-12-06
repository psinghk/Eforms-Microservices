package in.nic.ashwini.eForms.models;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
@Data	
public class UploadMultipleFilesBean {
	@NotEmpty
	private List<MultipartFile> infile;
	@Pattern(regexp = "[A-Z]+-[A-Z]+[0-9]+" ,message ="Wrong Format")
	private String registrationNo;
	@NotEmpty
	private String role;
	private String doc;
	private String docpath;
	private String status;
	public List<MultipartFile> getInfile() {
		return infile;
	}
	
	
}
