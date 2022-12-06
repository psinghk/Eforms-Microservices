package in.nic.ashwini.eForms.security.providers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import in.nic.ashwini.eForms.security.userdetailservice.SecurityUser;
import in.nic.ashwini.eForms.service.EncryptionService;
import lombok.Data;

@Data
public class RefreshTokenProvider implements AuthenticationProvider{
	
	@Autowired
	@Qualifier("userDetailsServiceForRefreshToken")
	public UserDetailsService userDetailsService;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		//UserDetails user = userDetailsService.loadUserByUsername(authentication.getName());
		String usernameForSSO = authentication.getName();
		String username1 = EncryptionService.decrypt(usernameForSSO);
		String[] usernameAndMobile = username1.split(":");
		String username = usernameAndMobile[0];
		List<GrantedAuthority> gaList = new ArrayList<>();
		gaList.add(new SimpleGrantedAuthority("ROLE_LOGIN_THROUGH_PARICHAY"));
		UserDetails user = new SecurityUser(username, usernameForSSO, gaList);
		authentication = new UsernamePasswordAuthenticationToken(user, "",authentication.getAuthorities());
		return authentication;
	}

	@Override
	public boolean supports(Class<?> authenticationType) {
		return UsernamePasswordAuthenticationToken.class.equals(authenticationType);
	}
}
