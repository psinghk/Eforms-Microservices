package in.nic.ashwini.eForms.db.slave.entities;

import java.time.LocalDateTime;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class ProfileDtoFromSlave {
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
	private String officePhone;
	private String residencePhone;
	private String hodName;
	private String hodEmail;
	private String hodMobile;
	private String hodDesignation;
	private String hodTelephone;
	private String roName;
	private String roEmail;
	private String roMobile;
	private String roDesignation;
	private String usEmail;
	private String usName;
	private String usMobile;
	private String usDesignation;
	private String usTelephone;
	private LocalDateTime creationTimeStamp;
	private LocalDateTime updationTimeStamp;
}