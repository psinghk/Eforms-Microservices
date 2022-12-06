package in.nic.ashwini.ldap.data;

import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Component
@Data
public final class UserFromZimbra  {
	private String dn;
	private String username;
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
	//private String sunAvailableServices = "";
}
