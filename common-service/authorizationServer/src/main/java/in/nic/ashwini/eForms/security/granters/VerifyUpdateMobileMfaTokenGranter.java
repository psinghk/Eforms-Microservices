package in.nic.ashwini.eForms.security.granters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.TokenStore;

import in.nic.ashwini.eForms.exception.GenericException;
import in.nic.ashwini.eForms.exception.InvalidCustomGrantException;
import in.nic.ashwini.eForms.service.EncryptionService;
import in.nic.ashwini.eForms.service.MfaService;
import in.nic.ashwini.eForms.service.UtilityService;
import in.nic.ashwini.eForms.service.ValidationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VerifyUpdateMobileMfaTokenGranter extends AbstractTokenGranter {
	private static final String GRANT_TYPE = "mobmfa";

	private final TokenStore tokenStore;
	private final ClientDetailsService clientDetailsService;
	private final AuthenticationManager authenticationManager;
	private final MfaService mfaService;
	private final UtilityService utilityService;
	private final ValidationService validationService;

	public VerifyUpdateMobileMfaTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,
			AuthenticationManager authenticationManager, MfaService mfaService, UtilityService utilityService,
			ValidationService validationService) {
		super(endpointsConfigurer.getTokenServices(), endpointsConfigurer.getClientDetailsService(),
				endpointsConfigurer.getOAuth2RequestFactory(), GRANT_TYPE);
		this.tokenStore = endpointsConfigurer.getTokenStore();
		this.clientDetailsService = endpointsConfigurer.getClientDetailsService();
		this.authenticationManager = authenticationManager;
		this.mfaService = mfaService;
		this.utilityService = utilityService;
		this.validationService = validationService;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		final String mfaToken = parameters.get("mfa_token");
		if (mfaToken != null) {
			OAuth2Authentication authentication = loadAuthentication(mfaToken);
			String username = EncryptionService.decrypt(authentication.getName());
			String[] decryptedUsername = username.split(";");
			String usernameDecrypted = decryptedUsername[0];
			log.info("USER : {}",usernameDecrypted);
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			if (!authorities.isEmpty() && authorities.contains(new SimpleGrantedAuthority("ROLE_UPDATE_MOBILE"))) {
				if (parameters.containsKey("mfa_code")) {
					int code = parseCode(parameters.get("mfa_code"));
					log.debug("OTP Verification started");
					if (mfaService.verifyCode(true, decryptedUsername[0], decryptedUsername[1], code)) {
						log.info("OTP VERIFICATION SUCCESSFUL");
						return getAuthentication(tokenRequest, authentication, usernameDecrypted);
					}
				} else {
					log.debug("Missing MFA code");
					throw new GenericException("Missing MFA code");
				}
				log.debug("Invalid MFA code");
				throw new InvalidGrantException("Invalid MFA code");
			}
			log.debug("You are not authorized for this grant!!!");
			throw new InvalidCustomGrantException("You are not authorized for this grant!!!");
		} else {
			log.debug("Missing MFA token");
			throw new GenericException("Missing MFA token");
		}
	}

	private OAuth2Authentication loadAuthentication(String accessTokenValue) {
		log.debug("Fetching OAuth2Authentication from token!!!");
		OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(accessTokenValue);

		if (accessToken == null) {
			log.debug("Invalid access token!!!");
			throw new GenericException("Invalid access token: " + accessTokenValue);
		} else if (accessToken.isExpired()) {
			this.tokenStore.removeAccessToken(accessToken);
			log.debug("Access token expired!!!");
			throw new GenericException("Access token expired: " + accessTokenValue);
		} else {
			OAuth2Authentication result = this.tokenStore.readAuthentication(accessToken);
			if (result == null) {
				log.debug("Invalid access token!!!");
				throw new GenericException("Invalid access token: " + accessTokenValue);
			}
			return result;
		}
	}

	private int parseCode(String codeString) {
		log.debug("Converting OTP from String to integer");
		try {
			return Integer.parseInt(codeString);
		} catch (NumberFormatException e) {
			log.debug("String to integer parsing exception occurred !!!");
			throw new InvalidCustomGrantException("Invalid MFA code");
		}
	}

	private OAuth2Authentication getAuthentication(TokenRequest tokenRequest, OAuth2Authentication authentication, String email) {
		log.debug("Authenticating the user and client credentials fetched through token!!!");
		List<GrantedAuthority> gaList = new ArrayList<>();
		gaList.add(new SimpleGrantedAuthority("ROLE_FULLY_AUTHENTICATED"));
		
		boolean govEmployee = utilityService.isGovEmployee(email);
		
		if(govEmployee) {
			gaList.add(new SimpleGrantedAuthority("ROLE_GOV_USER"));
		}else {
			gaList.add(new SimpleGrantedAuthority("ROLE_NONGOV_USER"));
		}
		
		gaList.add(new SimpleGrantedAuthority("ROLE_MOBILE_FORM"));
		Authentication userAuth = new UsernamePasswordAuthenticationToken(email, "",gaList);
		authentication = new OAuth2Authentication(authentication.getOAuth2Request(), userAuth);
		
		String clientId = authentication.getOAuth2Request().getClientId();
		if (clientId != null && clientId.equals(tokenRequest.getClientId())) {
			if (this.clientDetailsService != null) {
				try {
					this.clientDetailsService.loadClientByClientId(clientId);
				} catch (ClientRegistrationException e) {
					log.debug("Invalid client credentials !!!");
					throw new InvalidTokenException("Client not valid: " + clientId, e);
				}
			}
			return refreshAuthentication(authentication, tokenRequest);
		} else {
			log.debug("Client is missing or does not correspond to the MFA token");
			throw new InvalidCustomGrantException("Client is missing or does not correspond to the MFA token");
		}
	}

	private OAuth2Authentication refreshAuthentication(OAuth2Authentication authentication, TokenRequest request) {
		Set<String> scope = request.getScope();
		OAuth2Request clientAuth = authentication.getOAuth2Request().refresh(request);
		if (scope != null && !scope.isEmpty()) {
			Set<String> originalScope = clientAuth.getScope();
			if (originalScope == null || !originalScope.containsAll(scope)) {
				log.debug("Unable to narrow the scope of the client authentication to {}",scope);
				throw new InvalidScopeException(
						"Unable to narrow the scope of the client authentication to " + scope + ".", originalScope);
			}

			clientAuth = clientAuth.narrowScope(scope);
		}
		return new OAuth2Authentication(clientAuth, authentication.getUserAuthentication());
	}
}