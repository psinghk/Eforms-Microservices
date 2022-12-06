package in.nic.eForms.models;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import in.nic.eForms.customValidation.FinalIdValid;
import in.nic.eForms.customValidation.Ip;
import in.nic.eForms.customValidation.PrimaryIdValid;
import in.nic.eForms.customValidation.ValueValid;
import lombok.Data;

@Data
public class AdminFormBean {
	//@NotEmpty(message = "RegNumber should not be empty")
	private String regNumber;
	
	//(message = "IP should not be empty")
	//@Ip
	//private String ip;
	
	@NotEmpty(message = "PO should not be empty")
	//@ValueValid
	private String po;
	

	@NotEmpty(message = "BO can not be blank.")
	//@ValueValid
	private String bo;
	
	@NotEmpty(message = "Domain can not be blank.")
	//@Size(min=2,max=64, message="Length of the Domain must be between 2 and 64")
	//@Pattern(regexp = "^(?!:\\/\\/)([a-zA-Z0-9-\\_]+\\.){0,5}[a-zA-Z0-9-\\_][a-zA-Z0-9-\\_]+\\.[a-zA-Z]{2,64}?$", message = "Enter valid DNS URL [e.g.: demo.nic.in or demo.gov.in]")
	private String domain;
	
	
	//@NotEmpty(message = "Email can not be blank.")
	//@Pattern(regexp ="^[\\\\\\\\\\\\\\\\w\\\\\\\\\\\\\\\\-\\\\\\\\\\\\\\\\.\\\\\\\\\\\\\\\\+]+@[a-zA-Z0-9\\\\\\\\\\\\\\\\.\\\\\\\\\\\\\\\\-]+.[a-zA-z0-9]{2,4}$", message = "Enter Email Address [e.g: abc.xyz@gov.in")
	private String email;
	
	//@NotEmpty(message = "finalId can not be blank.")
	//@FinalIdValid
	private String finalEmailId; ;
	
	@NotEmpty(message = "primaryId can not be blank.")
	//@PrimaryIdValid
	private String primaryId;
	
	
	//@Pattern(regexp = "^[\\\\w\\\\-\\\\.\\\\+]+@[a-zA-Z0-9\\\\.\\\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.")
	private String statRemarks;
	
	@NotEmpty(message = "employmentType can not be blank.")
	private String employmentType;
	@NotEmpty(message = "prefferedEmail can not be blank.")
	private String prefferedEmail;
	@NotEmpty(message = "emailLinkedToMobile can not be blank.")
	private String emailLinkedToMobile;
	@NotEmpty(message = "description can not be blank.")
	private String description;//free/paid
	@NotEmpty(message = "aliasId can not be blank.")
	private String aliasId;	
	
	
	
}
