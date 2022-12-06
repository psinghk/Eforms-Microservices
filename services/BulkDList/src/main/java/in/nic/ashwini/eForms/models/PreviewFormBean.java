package in.nic.ashwini.eForms.models;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class PreviewFormBean {
	
	private List<MultipartFile> infile;   //excel file
	
	
	public List<MultipartFile> getInfile() {
		return infile;
	}
	
	private String organization;
//	@NotEmpty
	private String employment;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;
	private String remarks;
	private Boolean tnc;
	public Object getInfile;
	private List<Moderators> moderators;
	
	private List<Owners> owners;
	
	private String omName;
	private String omMobile;
	private String omEmail;
    private String  formType;
}
