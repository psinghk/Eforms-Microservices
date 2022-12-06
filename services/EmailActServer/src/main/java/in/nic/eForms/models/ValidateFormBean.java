package in.nic.eForms.models;

import javax.validation.constraints.Email;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import in.nic.eForms.customValidation.CheckPrefmail;
import lombok.Data;

@Data
@Validated
public class ValidateFormBean {
    
	@NotNull
	private String preferredEmail;
    @NotNull
	private String dor;
    @NotNull
	private String empType; 
	
	private MultipartFile workrOrder;
	
}
