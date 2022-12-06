package in.nic.ashwini.ldap.data;

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
	private String organization;
	private String nicwifi;
	private String zimotp;
	//@DateTimeFormat(pattern = "yyyy-MM-dd")
	private String dateOfBirth;
	//@DateTimeFormat(pattern = "yyyy-MM-dd")
	private String dateOfAccountExpiry;
	//@DateTimeFormat(pattern = "yyyy-MM-dd")
	private String dateOfRetirement;

//	public String getDateOfBirth() {
//		if (!dateOfBirth.isEmpty()) {
//			String[] d = dateOfBirth.split("-");
//			dateOfBirth = d[0] + d[1] + d[2] + "000000Z";
//		}
//		return dateOfBirth;
//	}
//
//	public String getDateOfAccountExpiry() {
//		if (!dateOfAccountExpiry.isEmpty()) {
//			String[] d = dateOfAccountExpiry.split("-");
//			dateOfAccountExpiry = d[0] + d[1] + d[2] + "000000Z";
//		}
//		return dateOfAccountExpiry;
//	}
//
//	public String getDateOfRetirement() {
//		if (!dateOfRetirement.isEmpty()) {
//			String[] d = dateOfRetirement.split("-");
//			dateOfRetirement = d[0] + d[1] + d[2] + "000000Z";
//		}
//		return dateOfRetirement;
//	}
}
