package in.nic.ashwini.eForms.models;

import javax.validation.constraints.NotEmpty;

import org.springframework.validation.annotation.Validated;

import lombok.Data;
@Validated
@Data
public class PreviewFormBean {
	
	@NotEmpty(message = "countryCode should not be empty")
	private String country_code;
//	@NotNull(message = "Mobile number should not be empty")
//	@Pattern(regexp = "^[+0-9]{13}$", message = "Mobile Should be 13 digits with country code")
	
	//@NotNull(message = "Mobile number should not be empty")
	//@Pattern(regexp = "^[+0-9]{10,13}$")
	@NotEmpty(message = "Remarks should not be empty")
	private String new_mobile;
	
	@NotEmpty(message = "otp should not be empty")
	private String otp;
	
	//@NotNull(message = "reason should not be empty")
	private String other_remarks;
	
	//@NotNull(message = "remarksFlag should not be empty")
	private String remarksFlag;
	
	private String organization;
	//@NotEmpty
	private String employment;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;
	@NotEmpty(message = "Remarks should not be empty")
	private String remarks;
	//@NotNull
	private Boolean tnc;
}
