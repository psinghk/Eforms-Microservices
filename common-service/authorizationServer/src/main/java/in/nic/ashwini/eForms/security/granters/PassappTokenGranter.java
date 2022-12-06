package in.nic.ashwini.eForms.security.granters;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;

import in.nic.ashwini.eForms.exception.CustomAuthException;
import in.nic.ashwini.eForms.exception.GenericException;
import in.nic.ashwini.eForms.exception.MfaRequiredException;
import in.nic.ashwini.eForms.service.MfaService;
import in.nic.ashwini.eForms.service.UtilityService;
import in.nic.ashwini.eForms.service.ValidationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PassappTokenGranter extends AbstractTokenGranter {
	private static final String GRANT_TYPE = "passapp";
	private final AuthenticationManager authenticationManager;
	private final MfaService mfaService;
	private final UtilityService utilityService;
	private final ValidationService validationService;

	public PassappTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,
			AuthenticationManager authenticationManager, MfaService mfaService, UtilityService utilityService,
			ValidationService validationService) {
		super(endpointsConfigurer.getTokenServices(), endpointsConfigurer.getClientDetailsService(),
				endpointsConfigurer.getOAuth2RequestFactory(), GRANT_TYPE);
		this.authenticationManager = authenticationManager;
		this.mfaService = mfaService;
		this.utilityService = utilityService;
		this.validationService = validationService;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		if (!parameters.containsKey("username")) {
			throw new GenericException("Missing username!!!");
		}
		String username = parameters.get("username");
		if (username == null || (username != null && username.isEmpty())) {
			log.debug("Username is empty");
			throw new CustomAuthException("Username can not be empty!!!");
		}

		String password = parameters.get("password");
		parameters.remove("password");
		
		Collection<GrantedAuthority> authorities = null;
		authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_PRE_AUTH"), new SimpleGrantedAuthority("ROLE_PASSAPP_USER"));
		Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password, authorities);

//		try {
//			userAuth = this.authenticationManager.authenticate(userAuth);
//		} catch (AccountStatusException | BadCredentialsException e) {
//			throw new BadCredentialsException(e.getMessage());
//		}

			OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
			String message = "";
			String mobile = utilityService.fetchMobile(username).trim();
			//String base64EncodedMobileNumbers = Base64.getEncoder().encodeToString(mobile.getBytes());
			
			if(!mobile.contains(",")) {
				mobile = utilityService.transformMobile(mobile);
			}else {
				throw new CustomAuthException("Invalid mobile number. Please get it updated first in NIC repository!!!");
			} 
			
			OAuth2AccessToken accessToken = getTokenServices()
					.createAccessToken(new OAuth2Authentication(storedOAuth2Request, userAuth));
			throw new MfaRequiredException(accessToken.getValue(), message, "");
	}
}
