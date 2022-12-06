package in.nic.eForms.models;

import java.time.LocalDate;
import java.util.List;
import javax.naming.Name;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class UserForCreate {
	
	private String username;
	
	private boolean nicEmployee;
	
	private String password;

	private Name dn;

	private String initials;
	
    private String firstName;
    
	private String middleName;
	
    private String lastName;
	
	private String displayName = "";
	
	private String cn = "";

	private String email;

	private List<String> aliases;

	private String mobile;

	private String postingLocation;
	
	private String telephoneNumber;

	private String officeAddress;

	private String homePhone;

	private String state;

	private String employeeCode;
	
	private String userInetStatus="Active";
	
	private String userMailStatus="Active";
	
	private String createtimestamp;

	private String mailforwardingaddress;
	private String mailMessageStore;
	private String icsCalendar;
	private String mailHost;
	private String description;
	private String title;
	private LocalDate nicDateOfBirth;
	private LocalDate nicDateOfRetirement;
	private String nicAccountExpDate;
	private String departmentNumber;
	private String icsExtendedUserPrefs;
	
	
}
