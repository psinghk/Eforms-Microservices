package in.nic.ashwini.eForms.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.naming.Name;


import org.springframework.stereotype.Component;

import lombok.Data;

@Component


@Data
public final class UserForCreate {
	
	
	private String username;
	
	
	private boolean nicEmployee;
	
	
	private String password;

	

	private String initials;
	
    private String firstName;
	
	private String middleName;
	
    private String lastName;
	
	
	private String displayName = "";
	
	private String cn;

	
	private String email;

	
	private List<String> aliases;

	private String mobile;

	
	private String postingLocation;
	private String telephoneNumber;

	////@Attribute(name = "postalAddress")
	private String officeAddress;

	////@Attribute(name = "homephone")
	private String homePhone;

	////@Attribute(name = "st")
	private String state;

	////@Attribute(name = "employeeNumber")
	private String employeeCode;
	
	////@Attribute(name="inetuserstatus")
	private String userInetStatus="Active";
	
	////@Attribute(name="mailuserstatus")
	private String userMailStatus="Active";
	
	private String createtimestamp;

	public void setCreatetimestamp(String createtimestamp) {
		Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.DATE, 1);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
        this.createtimestamp = format1.format(cal.getTime()) + "Z";
	}
	
	private String mailforwardingaddress;
	private String mailMessageStore;
	private String icsCalendar;
	private String mailHost;
	private String description;
	private String title;
	private String nicDateOfBirth;
	private String nicDateOfRetirement;
	private String nicAccountExpDate;
	private String departmentNumber;
	private String icsExtendedUserPrefs;
	
	private String psIncludeInGAB = "true";
	private String icsFirstDay = "2";
	////@Attribute(name="iplanet-am-modifiable-by")
	private String iplanet = "cn=Top-level Admin Role,dc=nic,dc=in";
	private String icsTimezone="Asia/Calcutta";
	private String icsStatus="active";
	private String inetCOS="NICUserMailCalendar";
	private String preferredLocale="en";
	private String mailAutoReplyMode="reply";
	private String preferredLanguage="en";
	private String mailDeferProcessing = "No";
	private String icsDWPHost = "cs1.nic.in";
	private String nicNewUser = "false";
	private String sunUCTimeZone = "Asia/Calcutta";
	private String sunUCDateDelimiter = "";
	private String sunUCDateFormat = "";
	private String sunUCDefaultEmailHandler = "uc";
	private String sunUCColorScheme = "";
	private String sunUCDefaultApplication = "mail";
	private String sunUCTimeFormat = "";
	private String mailQuota = "-1";
	private String mailallowedserviceaccess = "-smime:ALL$-pops:ALL$-pop:ALL$-imaps:ALL$-imap:ALL$-smtps:ALL$-smtp:ALL$-https:ALL$-http:ALL";
	private String davUniqueId;
	private String nicAccountCreationDate="100";
	private String nswmExtendedUserPrefs = "meSortOrder=R";
	private String maildeliveryoption = "forward";
	private String zimotp = "1";
}
