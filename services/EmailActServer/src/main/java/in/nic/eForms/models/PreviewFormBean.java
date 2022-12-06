package in.nic.eForms.models;

import javax.validation.constraints.Email;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;
import in.nic.eForms.customValidation.CheckPrefmail;
import lombok.Data;

@Data
public class PreviewFormBean {
	
	
	@NotEmpty(message = "Preferred Email1 can not be blank.")
	@Email
	private String preferredEmail;
	
	@NotNull
	private String dor;
	
	@NotEmpty(message = "Employee Type  should not be empty")
	private String empType; 
	
	private MultipartFile workrOrder;
	
	private String organization;
	private String employment;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;
	private String remarks;
	@NotNull
	private Boolean tnc;
	
}
