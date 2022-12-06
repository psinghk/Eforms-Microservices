package in.nic.eForms.models;

import java.util.List;
import lombok.Data;

@Data
public final class UserSearchBean  {
	private String username="";
	private String password="";
	private String initials = "";
    private String firstName = "";
	private String middleName = "";
    private String lastName = "";
	private String displayName = "";
	private String cn = "";
	private String mobile = "";
    private String email;
    private List<String> aliases;
	private List<String> mailalternateaddress;
	private String designation = "";
	private String postingLocation = "";
	private String telephoneNumber = "";
    private String officeAddress = "";
	private String homePhone = "";
    private String state = "";
    private String employeeCode = "";
	private String userInetStatus;
	private String userMailStatus;
	private List<String> allowedDomains;
	private String nicAccountExpDate = "";
	private String nicDateOfRetirement = "";
	private String nicDateOfBirth = "";
	private String mailallowedserviceaccess = "";
	private String nicwifi = "";
	private String inetsubscriberaccountid = "";
	private String nsroledn = "";
	private String createtimestamp = "";
	private String mailhost = "";
	private String o = "";
	private String mailmessagestore = "";
	private String nicLastLoginDetail = "";
	private String description = "";
	private String zimotp = "";
	private String davuniqueid = "";
	private String nicnewuser = "";
	private String remarks = "";
	private String associateddomain = "";

}
