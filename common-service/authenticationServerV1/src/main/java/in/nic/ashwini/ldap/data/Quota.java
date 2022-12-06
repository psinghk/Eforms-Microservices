package in.nic.ashwini.ldap.data;

import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Entry(objectClasses= {"top"})
@Data
public final class Quota  {
	@Id
    private Name dn;
	
	public String getDn() {
		return String.valueOf(this.dn);
	}
    
	private String sunAvailableServices;
	private String preferredMailHost;
	private String preferredMailMessageStore;
	@Attribute(name="sunAvailableDomainNames")
	private List<String> allowedDomains;
}
