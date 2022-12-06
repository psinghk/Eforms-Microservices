package in.nic.ashwini.ldap.data;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Entry(objectClasses= {"top"})
@Data
public final class UserForHomePageDA  {
	
	@Attribute(name="uid")
	private String username;
    
	@Id
    private Name dn;
	
	public String getDn() {
		return String.valueOf(this.dn);
	}
    
	@Attribute(name="cn")
    private String firstName;
	
	@Attribute(name="mail")
    private String email;
    
	private String mobile;
	
	@Attribute(name="inetuserstatus")
	private String userInetStatus;
	
	@Attribute(name="mailuserstatus")
	private String userMailStatus;
    
	@Attribute(name="title")
	private String designation;
	
	@Attribute(name="nicAccountExpDate")
	private String accountExpiryDate;
}
