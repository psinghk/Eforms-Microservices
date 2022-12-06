package in.nic.eForms.models;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import in.nic.eForms.customValidation.Conditional;
import in.nic.eForms.customValidation.ConditionalDesignation;
import in.nic.eForms.customValidation.ConditionalEmployeeCode;
import in.nic.eForms.customValidation.ConditionalMobile;
import in.nic.eForms.customValidation.FieldMatch;
import in.nic.eForms.customValidation.UniqueEmail;
import in.nic.eForms.exceptions.DobValid;
import in.nic.eForms.utils.LocalDateDeserializer;
import in.nic.eForms.utils.LocalDateSerializer;
import lombok.Data;

@Data
@FieldMatch(first = "preferredEmail1", second = "preferredEmail2", message = "preferredEmail1 and preferredEmail2 are same")

//@Conditional(selected = "reqUserType", values = {"other"}, required = {"applicantName"})
//@ConditionalMobile(selected = "reqUserType", values = {"other"}, required = {"applicantMobile"})
//@ConditionalDesignation(selected = "reqUserType", values = {"other"}, required = {"applicantDesign"})
//@ConditionalEmployeeCode(selected = "reqUserType", values = {"other"}, required = {"applicantEmpcode"})
public class PreviewFormBean {
	
	@NotEmpty(message = "Single User SubscriptionDetails or Request Type should not be empty")
	private String reqUserType;//singleUserSubscriptionDetails;//
	
	@NotEmpty(message = "Type  should not be empty")
	private String type;//com
	
	@NotEmpty(message = "Id Type  should not be empty")
	private String idType;
	
	@NotEmpty(message = "Employee Type  should not be empty")
	private String empType; 

	
	
	private String dob;
	
	
	private String dor;
	
	
	
	
	@NotEmpty(message = "Preferred Email1 can not be blank.")
	@Email
	@UniqueEmail
	private String preferredEmail1;//preferred_email1;
	
	@NotEmpty(message = "Preferred Email2 can not be blank.")
	@Email
	private String preferredEmail2;//preferred_email2;
	
	//by sunny
	//@NotEmpty(message = "Applicant Name should not be empty")
	
	//private String applicantName;
	
	//@NotEmpty(message = "Applicant Mobile should not be empty")
	
	//private String applicantMobile;
		
	//@NotEmpty(message = "Applicant Designation should not be empty")
	//private String applicantDesign;
	
	//@NotEmpty(message = "Applicant Employee Code should not be empty")
	//private String applicantEmpcode;
	
	//@NotEmpty(message = "Applicant Email should not be empty")
	
	//private String applicantEmail;
	
	//@NotEmpty(message = "Applicant State should not be empty")
	
	//private String applicantState;

	
	private String organizationCategory;
	private String ministryOrganization;
	private String departmentDivisionDomain;
	
	private String organization;
	private String employment;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;
	private String remarks;
	@NotNull
	private Boolean tnc;
	

	/*
	 * @NotEmpty(message = "Userid can not be blank.")
	 * 
	 * @Uid private String preferred_uid1;
	 * 
	 * @NotEmpty(message = "Userid can not be blank.")
	 * 
	 * @Uid private String preferred_uid2;
	 */
	
	
}
