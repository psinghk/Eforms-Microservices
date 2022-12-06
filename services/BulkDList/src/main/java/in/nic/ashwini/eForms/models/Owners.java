package in.nic.ashwini.eForms.models;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import in.nic.ashwini.eForms.customValidation.EmailValid;
import lombok.Data;

@Data

public class Owners {
	
	    @Pattern(regexp = "^[a-zA-Z0-9 .,]{1,50}$",message = "Please enter Moderator name")
	    @NotNull(message = "Moderator name should not be empty")
	    private String owner_name;
	    
	    @EmailValid
	    private String owner_email;
	    
	    @Pattern(regexp = "^[+0-9]{10}$",message = "Please enter Moderator mobile")
		@NotNull(message = "Moderator mobile should not be empty")
	    private String owner_mobile;	
}
