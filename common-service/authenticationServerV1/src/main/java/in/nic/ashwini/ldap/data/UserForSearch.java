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
@Entry(objectClasses= {"top"})
@Data
public final class UserForSearch  {
	
	@Attribute(name="uid")
	private String username;
    
	@JsonIgnore
	@Attribute(name="userPassword")
	private String password;
	
	@Id
    private Name dn;
	
	public String getDn() {
		return String.valueOf(this.dn);
	}
	
	private String initials = "";
	@Attribute(name="givenname")
    private String firstName = "";
	@Attribute(name="nicmiddlename")
	private String middleName = "";
	@Attribute(name="sn")
    private String lastName = "";
	
	@Attribute(name="displayname")
	private String displayName = "";
	
	private String cn = "";
	
	private String mobile = "";
    
	@Attribute(name="mail")
    private String email;
    
	@Attribute(name="mailequivalentaddress")
    private List<String> aliases;
	private List<String> mailalternateaddress;
	
	@Attribute(name="title")
	private String designation = "";
	
	@Attribute(name="nicCity")
	private String postingLocation = "";
	private String telephoneNumber = "";
    
	@Attribute(name="postalAddress")
    private String officeAddress = "";
    
	@Attribute(name="homephone")
	private String homePhone = "";
    
	@Attribute(name="st")
    private String state = "";
    
	@Attribute(name="employeeNumber")
    private String employeeCode = "";
	
	@Attribute(name="inetuserstatus")
	private String userInetStatus;
	
	@Attribute(name="mailuserstatus")
	private String userMailStatus;
    
	//private List<String> objectclass;
	
	@Attribute(name="sunAvailableDomainNames")
	@JsonIgnore
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
	@Attribute(name="inetsubscriberaccountid")
	private String remarks = "";
	private String associateddomain = "";
	//private String sunAvailableServices = "";
}
