package in.nic.ashwini.ldap.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component

@Entry(objectClasses = { "top", "person", "organizationalPerson", "inetOrgPerson", "userpresenceprofile",
		"sunucpreferences", "iplanet-am-user-service", "iplanet-am-managed-person", "icscalendaruser", "inetadmin",
		"sunimuser", "nicemailuser", "sunamauthaccountlockout", "inetuser", "inetlocalmailrecipient", "sunpresenceuser",
		"iplanetpreferences", "ipuser", "inetsubscriber", "inetmailuser", "daventity" })
@Data
public final class UserForCreateForAppUsers {
	
	@Attribute(name = "uid")
	private String username;
	
	@Transient
	private boolean nicEmployee;
	
	@Attribute(name = "userPassword")
	private String password;

	@Id
	private Name dn;

	public String getDn() {
		return String.valueOf(this.dn);
	}

	private String initials;
	@Attribute(name="givenname")
    private String firstName;
	@Attribute(name="nicmiddlename")
	private String middleName;
	@Attribute(name="sn")
    private String lastName;
	
	@Attribute(name="displayname")
	private String displayName = "";
	
	private String cn = "";

	@Attribute(name = "mail")
	private String email;

	@Attribute(name = "mailequivalentaddress")
	private List<String> aliases;

	private String mobile;

	@Attribute(name = "nicCity")
	private String postingLocation;
	private String telephoneNumber;

	@Attribute(name = "postalAddress")
	private String officeAddress;

	@Attribute(name = "homephone")
	private String homePhone;

	@Attribute(name = "st")
	private String state;

	@Attribute(name = "employeeNumber")
	private String employeeCode;
	
	@Attribute(name="inetuserstatus")
	private String userInetStatus="Active";
	
	@Attribute(name="mailuserstatus")
	private String userMailStatus="Active";
	
	private String createtimestamp;

	public void setCreatetimestamp(String createtimestamp) {
		Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.DATE, 1);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
        this.createtimestamp = format1.format(cal.getTime()) + "Z";
	}
	
	private String icsCalendar;
	private String description;
	private String title;
	private String nicDateOfBirth;
	private String nicDateOfRetirement;
	private String nicAccountExpDate;
	private String departmentNumber;
	private String icsExtendedUserPrefs;
	private String davUniqueId;
	
	private String mailMessageStore = "without-container";
	private String mailHost = "ms22.nic.in";
	@Attribute(name="iplanet-am-modifiable-by")
	private String iplanet = "cn=Top-level Admin Role,dc=nic,dc=in";
	//private String psIncludeInGABicsFirstDay = "2";
	//private String icsStatus="active";
	private String inetCOS="NICUserMailCalendar";
	private String preferredLocale="en";
	private String preferredLanguage="en";
	//private String mailDeferProcessing = "No";
	//private String icsDWPHost = "cs1.nic.in";
	private String nicNewUser = "true";
	private String sunUCTimeZone = "Asia/Calcutta";
	//private String sunUCDateDelimiter = "";
	//private String sunUCDateFormat = "";
	//private String sunUCColorScheme = "";
	//private String sunUCTimeFormat = "";
	private String mailallowedserviceaccess = "-smime:ALL$-pops:ALL$-pop:ALL$-imaps:ALL$-imap:ALL$-smtps:ALL$-smtp:ALL$-https:ALL$-http:ALL";
	//private String nicAccountCreationDate="100";
	//private String nswmExtendedUserPrefs = "meSortOrder=R";
	//private String maildeliveryoption = "forward";
	private String zimotp = "1";
}
