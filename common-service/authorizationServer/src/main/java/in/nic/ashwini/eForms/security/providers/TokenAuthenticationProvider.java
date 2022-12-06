package in.nic.ashwini.eForms.security.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class TokenAuthenticationProvider implements AuthenticationProvider{
	
	@Autowired
	@Qualifier("jpaUserDetailsService")
	public UserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UserDetails user = userDetailsService.loadUserByUsername(authentication.getName());
		authentication = new UsernamePasswordAuthenticationToken(user, "",user.getAuthorities());
		return authentication;
	}

	@Override
	public boolean supports(Class<?> authenticationType) {
		return UsernamePasswordAuthenticationToken.class.equals(authenticationType);
	}
}
