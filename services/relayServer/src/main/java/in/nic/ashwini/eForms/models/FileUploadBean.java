package in.nic.ashwini.eForms.models;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
@Data
public class FileUploadBean {

	@NotEmpty
	private List<MultipartFile> uploadedFilename;
	@Pattern(regexp = "[A-Z]+-[A-Z]+[0-9]+" ,message ="Wrong Format")
	private String regno;
	private String doc;
	private String docpath;
	private String status;
	public List<MultipartFile> getUploadedFilename() {
		return uploadedFilename;
	}
	
	
	
}
