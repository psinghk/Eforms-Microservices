package in.nic.ashwini.eForms.security.userdetailservice;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Data;

@Data
public class SecurityUser extends User {

	private static final long serialVersionUID = 1L;
	
	private String userDetailsForSSO = "";
	
	public SecurityUser(String email, String password, List<GrantedAuthority> authorities) {
		super(email, "", authorities);
		this.userDetailsForSSO = password;
	}
	
	
}
