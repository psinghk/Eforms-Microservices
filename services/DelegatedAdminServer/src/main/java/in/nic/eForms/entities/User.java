package in.nic.eForms.entities;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class User {
	private String username;
	private boolean nicEmployee;
	private String password;
	private String dn;
	private String initials;
	private String firstName;
	private String middleName;
	private String lastName;
	private String displayName;
	private String cn;
	private String designation;
	private String email;
	private List<String> aliases;
	private String mobile;
	private String postingLocation;
	private String telephoneNumber;
	private String officeAddress;
	private String homePhone;
	private String state;
	private String employeeCode;
	private String userInetStatus;
	private String userMailStatus;
	private String o;
	private String nicwifi;
	private String nicDateOfBirth;
	private String nicAccountExpDate;
	private String nicDateOfRetirement;
	private String mailallowedserviceaccess;
}
