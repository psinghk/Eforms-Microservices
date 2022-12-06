package in.nic.ashwini.eForms.models;

import java.time.LocalDateTime;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class ProfileDto {
	private Long id;
	@NotNull(message = "Email address should not be empty")
	@Email(message = "Enter Email Address in correct format")
	private String email;
	@NotNull(message = "Mobile number should not be empty")
	@Pattern(regexp = "^[+0-9]{13}$", message = "Mobile Should be 13 digits with country code")
	private String mobile;
	@NotNull(message = "Initial should not be empty")
	@Pattern(regexp = "[a-zA-Z .,]{1,50}", message = "Please enter Initial in correct format")
	private String name;
	@NotNull(message = "Designation should not be empty")
	@Pattern(regexp = "^[a-zA-Z -.,]{1,50}$", message = "Please enter designation in correct format, Alphanumeric(,.) allowed  [limit 1-50]ss")
	private String designation;
	@NotNull(message = "Employment should not be empty")
	@Pattern(regexp = "^[a-zA-Z0-9 .,-_&]{1,50}$", message = "Please enter employment in correct format, Alphanumeric(,.) allowed  [limit 1-50]")
	private String employment;
	private String ministry;
	private String department;
	private String otherDept;
	private String state;
	private String organization;
	@NotNull(message = "Employee code should not be empty")
	@Pattern(regexp = "[0-9a-zA-Z ]{1,8}$", message = "Please enter employee code in correct format, Alphanumeric allowed limit[1-8]")
	private String empCode;
	@NotNull(message = "State should not be empty")
	@Pattern(regexp = "^[a-zA-Z0-9 .,-_&]{2,100}$", message = "Please enter state in correct format , Alphanumeric(.,-_&) allowed [limit 2-100]")
	private String postingState;
	@NotNull(message = "City should not be empty")
	@Pattern(regexp = "^[a-zA-Z0-9 .,-_&]{2,100}$", message = "Please enter city in correct format , Alphanumeric(.,-_&) allowed  [limit 2-100]")
	private String city;
	@NotNull(message = "Address should not be empty")
	@Pattern(regexp = "^[a-zA-Z0-9 .,-_&]{2,100}$", message = "Please enter address in correct format, Alphanumeric(.,-_&) allowed  [limit 2-100]")
	private String address;
	@NotNull(message = "Pin should not be empty")
	@Pattern(regexp = "^[0-9]{1,6}$", message = "Please enter pin in correct format limit 6")
	private String pin;
	@NotNull
	@Pattern(regexp = "[a-zA-Z .,]{1,50}", message = "Please enter hod name in correct format [Alphanumeric [limit 1-50]")
	private String hodName;
	@NotNull(message = "Hod mail address should not be empty")
	@Email(message = "Enter Hod email Address in correct format")
	private String hodEmail;
	@NotNull(message = "Hod mobile number should not be empty")
	@Pattern(regexp = "^[+0-9]{13}$", message = "Hod mobile Should be 13 digits with country code")
	private String hodMobile;
	@NotNull(message = "Hod designation should not be empty")
	@Pattern(regexp = "^[a-zA-Z -.,]{1,50}$", message = "Please enter hod designation in correct format")
	private String hodDesignation;
	@NotNull
	@Pattern(regexp = "^[+0-9]{3,5}[-]([0-9]{6,15})$", message = "")
	private String hodTelephone;
	@NotNull
	@Pattern(regexp = "[a-zA-Z .,]{1,50}", message = "Please enter hod name in correct format [Alphanumeric [limit 1-50]")
	private String roName;
	@NotNull(message = "Hod mail address should not be empty")
	@Email(message = "Enter Hod email Address in correct format")
	private String roEmail;
	@NotNull(message = "Hod mobile number should not be empty")
	@Pattern(regexp = "^[+0-9]{13}$", message = "Hod mobile Should be 13 digits with country code")
	private String roMobile;
	@NotNull(message = "Hod designation should not be empty")
	@Pattern(regexp = "^[a-zA-Z -.,]{1,50}$", message = "Please enter hod designation in correct format")
	private String roDesignation;
	
	//private Date creationTimeStamp;
//	@NotNull(message = "Under Secretary mail address should not be empty")
//	@Email(message = "Enter Under Secretary email Address in correct format")
	private String usEmail;
//	@NotNull
//	@Pattern(regexp = "[a-zA-Z .,]{1,50}", message = "Please enter Under Secretary name in correct format [Alphanumeric [limit 1-50]")
	private String usName;
//	@NotNull(message = "Under Secretary mobile number should not be empty")
//	@Pattern(regexp = "^[+0-9]{13}$", message = "Under Secretary mobile Should be 13 digits with country code")
	private String usMobile;
//	@NotNull(message = "Under Secretary designation should not be empty")
//	@Pattern(regexp = "^[a-zA-Z -.,]{1,50}$", message = "Please enter Under Secretary designation in correct format")
	private String usDesignation;
//	@NotNull
//	@Pattern(regexp = "^[+0-9]{3,5}[-]([0-9]{6,15})$", message = "")
	private String usTelephone;
	
	private LocalDateTime creationTimeStamp;
	
	private LocalDateTime updationTimeStamp;
	}
