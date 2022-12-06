package in.nic.ashwini.eForms.security.tokenenhancer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.web.client.RestTemplate;

import in.nic.ashwini.eForms.service.EncryptionService;

public class CustomTokenConverter extends JwtAccessTokenConverter {

	@Value("${admin.url}")
	private String ADMIN_URL;

	@Autowired
	@LoadBalanced
	private RestTemplate restTemplate;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		final Map<String, Object> additionalInfo = new HashMap<>();
		Collection<GrantedAuthority> gaList = authentication.getAuthorities();
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_FULLY_AUTHENTICATED"))) {
			Set<String> allowedForms = fetchAllowedForms(authentication.getName(), gaList);
			String allowedFormsInString = String.join(":", allowedForms);
			additionalInfo.put("allowedForms", EncryptionService.encrypt(allowedFormsInString));
		}
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
		return super.enhance(accessToken, authentication);
	}

//	@Override
//	public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
//		final OAuth2Authentication authentication =    super.extractAuthentication(map);
//	    final Map<String, Object> details = new HashMap<>();
//	    details.put("allowedForms", map.get("allowedForms"));
//	    authentication.setDetails(details);
//	    return authentication;
//	}

	private Set<String> fetchAllowedForms(String email, Collection<GrantedAuthority> authorities) {
		String uri = "";
		if(authorities.contains(new SimpleGrantedAuthority("ROLE_SUPERADMIN"))) {
			uri = ADMIN_URL + "/fetchAllowedForms?email=" + email +"&role=" + "ROLE_SUPERADMIN";
			return restTemplate.getForObject(uri, Set.class);
		}else if(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			uri = ADMIN_URL + "/fetchAllowedForms?email=" + email +"&role=" + "ROLE_ADMIN";
			return restTemplate.getForObject(uri, Set.class);
		} else if(authorities.contains(new SimpleGrantedAuthority("ROLE_SUPPORT"))) {
			uri = ADMIN_URL + "/fetchAllowedForms?email=" + email +"&role=" + "ROLE_SUPPORT";
			return restTemplate.getForObject(uri, Set.class);
		} else {
			return new HashSet<>();
		}
	}

}
