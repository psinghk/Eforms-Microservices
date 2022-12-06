package in.nic.ashwini.ldap.data;

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
public final class UserForHodDetails  {
	@JsonIgnore
	@Id
    private Name dn;
	
	@Attribute(name="cn")
    private String firstName;
	
	@Attribute(name = "mail")
	private String email;
	
	private String mobile;
	
	@Attribute(name="title")
	private String designation;
	
	private String telephoneNumber;
}
