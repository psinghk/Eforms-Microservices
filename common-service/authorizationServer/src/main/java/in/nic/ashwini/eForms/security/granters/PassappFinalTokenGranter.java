package in.nic.ashwini.eForms.security.granters;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
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

import in.nic.ashwini.eForms.db.master.repositories.ErrorOtpDetailsRepository;
import in.nic.ashwini.eForms.db.master.repositories.LoginDetailsRepository;
import in.nic.ashwini.eForms.exception.GenericException;
import in.nic.ashwini.eForms.exception.InvalidCustomGrantException;
import in.nic.ashwini.eForms.service.MfaService;
import in.nic.ashwini.eForms.service.UtilityService;
import in.nic.ashwini.eForms.service.ValidationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PassappFinalTokenGranter extends AbstractTokenGranter {
	private static final String GRANT_TYPE = "passapp_final";

	private final TokenStore tokenStore;
	private final ClientDetailsService clientDetailsService;
	private final AuthenticationManager authenticationManager;
	private final MfaService mfaService;
	private final UtilityService utilityService;
	private final ValidationService validationService;
	private final LoginDetailsRepository loginDetailsRepository;
	private final ErrorOtpDetailsRepository errorOtpDetailsRepository;

	public PassappFinalTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,
			AuthenticationManager authenticationManager, MfaService mfaService, UtilityService utilityService,
			ValidationService validationService, LoginDetailsRepository loginDetailsRepository, ErrorOtpDetailsRepository errorOtpDetailsRepository) {
		super(endpointsConfigurer.getTokenServices(), endpointsConfigurer.getClientDetailsService(),
				endpointsConfigurer.getOAuth2RequestFactory(), GRANT_TYPE);
		this.tokenStore = endpointsConfigurer.getTokenStore();
		this.clientDetailsService = endpointsConfigurer.getClientDetailsService();
		this.authenticationManager = authenticationManager;
		this.mfaService = mfaService;
		this.utilityService = utilityService;
		this.validationService = validationService;
		this.loginDetailsRepository = loginDetailsRepository;
		this.errorOtpDetailsRepository = errorOtpDetailsRepository;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		final String mfaToken = parameters.get("mfa_token");
		if (mfaToken != null) {
			String ip = parameters.get("ip");
			OAuth2Authentication authentication = loadAuthentication(mfaToken);
			String username = authentication.getName();
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			LocalDateTime currentTime = LocalDateTime.now();
			LocalDateTime newTime = currentTime.minusHours(1l);
			if (!authorities.isEmpty() && authorities.contains(new SimpleGrantedAuthority("ROLE_PRE_AUTH"))) {
				return getAuthentication(tokenRequest, authentication, ip);
			}
			log.debug("You are not authorized for this grant!!!");
			throw new InvalidCustomGrantException("You are not authorized for this grant!!!");
		} else {
			log.debug("Missing MFA token!!!");
			throw new GenericException("Missing MFA token");
		}
	}

	private OAuth2Authentication loadAuthentication(String accessTokenValue) {
		log.debug("Fetching OAuth2Authentication from token!!!");
		OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(accessTokenValue);

		if (accessToken == null) {
			log.debug("access token can not be null");
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

	private OAuth2Authentication getAuthentication(TokenRequest tokenRequest, OAuth2Authentication authentication, String ip) {
		log.debug("Authenticating the user and client credentials fetched through token!!!");
		Collection<GrantedAuthority> authorities = null;
		authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_PASSAPP_FULLY_AUTHENTICATED"));
		Authentication userAuth = new UsernamePasswordAuthenticationToken(authentication.getName()+";"+ip, "", authorities);
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