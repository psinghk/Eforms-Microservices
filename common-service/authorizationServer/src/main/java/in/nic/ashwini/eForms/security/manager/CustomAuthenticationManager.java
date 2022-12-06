package in.nic.ashwini.eForms.security.manager;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import in.nic.ashwini.eForms.service.EncryptionService;

public class CustomAuthenticationManager implements AuthenticationManager {

	@Autowired
	private AuthenticationProvider ldapAuthenticationProvider;

	@Autowired
	private AuthenticationProvider tokenAuthenticationProvider;

	@Autowired
	private AuthenticationProvider refreshTokenProvider;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		if (authorities.isEmpty() || !authorities.contains(new SimpleGrantedAuthority("ROLE_PRE_AUTH"))) {
			if (authorities.contains(new SimpleGrantedAuthority("ROLE_AUTH_THROUGH_SSO"))
					|| authorities.contains(new SimpleGrantedAuthority("ROLE_LOGIN_THROUGH_PARICHAY"))
					|| authorities.contains(new SimpleGrantedAuthority("ROLE_FULLY_AUTHENTICATED"))) {
				if (authorities.contains(new SimpleGrantedAuthority("ROLE_AUTH_THROUGH_SSO"))
						|| authorities.contains(new SimpleGrantedAuthority("ROLE_FULLY_AUTHENTICATED"))) {
					authentication = tokenAuthenticationProvider.authenticate(authentication);
				} else {
					authentication = refreshTokenProvider.authenticate(authentication);
				}
			} else {
				authentication = ldapAuthenticationProvider.authenticate(authentication);
			}
		} else {
			authentication = tokenAuthenticationProvider.authenticate(authentication);
		}

		if (authentication == null) {
			throw new BadCredentialsException("Invalid Credentials or Authority!!!");
		}
		return authentication;
	}
}
