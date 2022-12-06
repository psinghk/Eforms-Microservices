package in.nic.ashwini.ldap.data;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Entry(objectClasses = { "top" })
@Data
public final class MobileAndName {

	@Id
	private Name dn;

	public String getDn() {
		return String.valueOf(this.dn);
	}

	private String cn = "";

	private String mobile = "";
	

}
