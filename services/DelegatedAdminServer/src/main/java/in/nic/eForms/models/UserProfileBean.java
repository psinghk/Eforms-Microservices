package in.nic.eForms.models;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
public class UserProfileBean {
	@NotEmpty
	private String adminId;
	@NotEmpty
	private String adminEmail;
	@NotEmpty
	private String adminIp;
	private String adminMobile;
	@NotEmpty
	private String dateTime;
	private String departmnt;
	private String designtn;
	private String employeeCode;
	private String firstName;
	private String lastName;
	private String middleName;
	private String mob;
	private String Addess;
	private String smsMid;
	private String smsMobile;
	private String telephoneNumber;
	@NotEmpty
	private String uid;
}
