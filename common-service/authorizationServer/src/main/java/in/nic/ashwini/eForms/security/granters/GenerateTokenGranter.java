package in.nic.ashwini.eForms.security.granters;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;

import in.nic.ashwini.eForms.service.EncryptionService;
import in.nic.ashwini.eForms.service.MfaService;
import in.nic.ashwini.eForms.service.UtilityService;
import in.nic.ashwini.eForms.service.ValidationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenerateTokenGranter extends AbstractTokenGranter {
	private static final String GRANT_TYPE = "generate";
	private final AuthenticationManager authenticationManager;
	private final MfaService mfaService;
	private final UtilityService utilityService;
	private final ValidationService validationService;
	
	public GenerateTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,
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
		if(!parameters.containsKey("username")) {
			throw new InvalidRequestException("Missing username!!!");
		}
		String username = parameters.get("username");
		if(username == null || (username != null && username.isEmpty())) {
			log.debug("Email address is empty");
			throw new BadCredentialsException("Email address can not be empty!!!");
		}
		if(!validationService.isFormatValid("email", username)) {
			log.debug("Invalid Email format.!!!");
			throw new BadCredentialsException("Invalid Email format.!!!");
		}
		if(!parameters.containsKey("service")) {
			log.debug("Missing service name!!!");
			throw new InvalidRequestException("Missing service name!!!");
		}
		String serviceName = parameters.get("service");
		if(serviceName == null || (serviceName != null && serviceName.isEmpty())) {
			log.debug("Service Name is empty!!!");
			throw new BadCredentialsException("Service Name can not be empty!!!");
		}
		if(!parameters.containsKey("browserId")) {
			log.debug("Missing browser ID!!!");
			throw new InvalidRequestException("Missing browser ID!!!");
		}
		String browserId = parameters.get("browserId");
		if(browserId == null || (browserId != null && browserId.isEmpty())) {
			log.debug("Browser ID is empty!!!");
			throw new BadCredentialsException("Browser ID can not be empty!!!");
		}
		if(!parameters.containsKey("sessionId")) {
			log.debug("Session ID!!!");
			throw new InvalidRequestException("Missing session ID!!!");
		}
		String sessionId = parameters.get("sessionId");
		if(sessionId == null || (sessionId != null && sessionId.isEmpty())) {
			log.debug("Session ID is empty!!!");
			throw new BadCredentialsException("Session ID can not be empty!!!");
		}
		if(!parameters.containsKey("localTokenId")) {
			log.debug("Local Token ID!!!");
			throw new InvalidRequestException("Missing Local Token ID!!!");
		}
		String localTokenId = parameters.get("localTokenId");
		if(localTokenId == null || (localTokenId != null && localTokenId.isEmpty())) {
			log.debug("Local Token ID is empty!!!");
			throw new BadCredentialsException("Local Token ID can not be empty!!!");
		}
		if(!parameters.containsKey("mobile")) {
			log.debug("Mobile number!!!");
			throw new InvalidRequestException("Missing mobile number!!!");
		}
		String mobile = parameters.get("mobile");
		if(mobile == null || (mobile != null && mobile.isEmpty())) {
			log.debug("Mobile number is empty!!!");
			throw new BadCredentialsException("Mobile number can not be empty!!!");
		}
		if(!parameters.containsKey("ip")) {
			log.debug("IP!!!");
			throw new InvalidRequestException("Missing IP!!!");
		}
		String ip = parameters.get("ip");
		if(ip == null || (ip != null && ip.isEmpty())) {
			log.debug("IP is empty!!!");
			throw new BadCredentialsException("IP can not be empty!!!");
		}
		Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_AUTH_THROUGH_SSO"));
		Authentication userAuth = new UsernamePasswordAuthenticationToken(EncryptionService.encrypt(username+";"+mobile+";localTokenId=>"+localTokenId+";userName=>"+username+";service=>"+serviceName+";browserId=>"+browserId+";sessionId=>"+sessionId+";ip=>"+ip), "", authorities);

		try {
			userAuth = this.authenticationManager.authenticate(userAuth);
		
		} catch (AccountStatusException | BadCredentialsException e) {
			throw new BadCredentialsException(e.getMessage());
		}

		if (userAuth != null && userAuth.isAuthenticated()) {
			OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
			return new OAuth2Authentication(storedOAuth2Request, userAuth);
		}
		throw new InvalidGrantException("Could not authenticate user: " + username);
	}
}
