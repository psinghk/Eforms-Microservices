package in.nic.ashwini.eForms.security.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import in.nic.ashwini.eForms.exception.CustomAuthException;
import in.nic.ashwini.eForms.service.EncryptionService;
import in.nic.ashwini.eForms.service.UtilityService;

public class LdapAuthenticationProvider implements AuthenticationProvider{
	
	@Autowired
	private UtilityService utilityService;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String mobile = "";
		Boolean authenticated = utilityService.authenticateThroughLdap(authentication.getName(),authentication.getCredentials().toString());
		//Boolean authenticated = true;
		if(authenticated) {
			if(!utilityService.isSupportEmail(username)) {
				mobile = utilityService.fetchMobile(username);
				mobile = utilityService.transformMobile(mobile);
				
				/*
				 * Generate random number OTP and send it to mobile
				 * 
				 * */
			}
			
			authentication = new UsernamePasswordAuthenticationToken(EncryptionService.encrypt(username+";"+mobile),authentication.getCredentials(),null);
			return authentication;
		}
		throw new CustomAuthException("Invalid credentials!!!");
	}

	@Override
	public boolean supports(Class<?> authenticationType) {
		return UsernamePasswordAuthenticationToken.class.equals(authenticationType);
	}
	
	
}
