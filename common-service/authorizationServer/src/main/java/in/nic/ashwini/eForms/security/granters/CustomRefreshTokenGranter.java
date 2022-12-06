package in.nic.ashwini.eForms.security.granters;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.exception.CustomAuthException;
import in.nic.ashwini.eForms.exception.GenericException;
import in.nic.ashwini.eForms.exception.InvalidCustomGrantException;
import in.nic.ashwini.eForms.service.EncryptionService;
import in.nic.ashwini.eForms.service.MfaService;
import in.nic.ashwini.eForms.service.UtilityService;
import in.nic.ashwini.eForms.service.ValidationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRefreshTokenGranter extends AbstractTokenGranter {
	private static final String GRANT_TYPE = "custom_refresh_token";

	private final TokenStore tokenStore;
	private final ClientDetailsService clientDetailsService;
	private final AuthenticationManager authenticationManager;
	private final MfaService mfaService;
	private final UtilityService utilityService;
	private final ValidationService validationService;
	// private final CustomTokenConverter customTokenConverter;

	public CustomRefreshTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,
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
		// this.customTokenConverter = endpointsConfigurer.getTokenEnhancer();
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		final String mfaToken = parameters.get("refresh_token");
		if (mfaToken != null) {
			OAuth2Authentication authentication = loadAuthentication(mfaToken);

			String[] parts = mfaToken.split("\\.", 0);

			byte[] bytes = Base64.getUrlDecoder().decode(parts[1]);
			String decodedString = new String(bytes, StandardCharsets.UTF_8);

			System.out.println("Decoded: " + decodedString);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> jsonMap = null;

			try {
				// convert JSON string to Map
				jsonMap = mapper.readValue(decodedString, new TypeReference<Map<String, Object>>() {
				});
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}

			return getAuthentication(tokenRequest, authentication, jsonMap, decodedString);
		} else {
			log.debug("Invalid refresh token");
			throw new GenericException("Invalid refresh token");
		}
	}

	private OAuth2Authentication loadAuthentication(String refreshTokenValue) {
		log.debug("Fetching OAuth2Authentication from token!!!");
		OAuth2RefreshToken refreshToken = this.tokenStore.readRefreshToken(refreshTokenValue);

		if (refreshToken == null) {
			log.debug("Invalid refresh token");
			throw new GenericException("Invalid refresh token: " + refreshTokenValue);
		}
		OAuth2Authentication authentication = tokenStore.readAuthenticationForRefreshToken(refreshToken);
		if (authentication == null) {
			log.debug("Invalid refresh token");
			throw new GenericException("Invalid refresh token: " + refreshTokenValue);
		}
		return authentication;
	}

	private OAuth2Authentication getAuthentication(TokenRequest tokenRequest, OAuth2Authentication authentication,
			Map<String, Object> map, String decodedString) {
		log.debug("Authenticating the user and client credentials fetched through token!!!");
		Authentication userAuth = null;
		if (decodedString.contains("ROLE_LOGIN_THROUGH_PARICHAY")
				&& decodedString.contains("ROLE_FULLY_AUTHENTICATED")) {
			String username = map.get("username").toString().trim();
			String mobile = map.get("mobile").toString().trim();
			mobile = utilityService.transformMobile(mobile);
			String localTokenId = map.get("localTokenId").toString().trim();
			String serviceName = map.get("service").toString().trim();
			String browserId = map.get("browserId").toString().trim();
			String sessionId = map.get("sessionId").toString().trim();
			String ip = map.get("ip").toString().trim();
			userAuth = new UsernamePasswordAuthenticationToken(EncryptionService.encrypt(
					username + ";" + mobile + ";localTokenId=>" + localTokenId + ";userName=>" + username + ";service=>"
							+ serviceName + ";browserId=>" + browserId + ";sessionId=>" + sessionId + ";ip=>" + ip),
					"", authentication.getAuthorities());
		} else if (decodedString.contains("ROLE_FULLY_AUTHENTICATED")) {
			String username = map.get("username").toString().trim();
			String serviceName = map.get("service").toString().trim();
			String ip = map.get("ip").toString().trim();
			String mobile = map.get("mobile").toString().trim();
			userAuth = new UsernamePasswordAuthenticationToken(
					EncryptionService.encrypt(username+";"+mobile+";userName=>" + username + ";service=>" + serviceName + ";ip=>" + ip), "",
					authentication.getAuthorities());
		} else {
			throw new CustomAuthException("Refresh option not available to you!!!");
		}

		try {
			userAuth = this.authenticationManager.authenticate(userAuth);
		} catch (AccountStatusException | BadCredentialsException e) {
			throw new CustomAuthException(e.getMessage());
		}

		if (userAuth != null && userAuth.isAuthenticated()) {
			String clientId = authentication.getOAuth2Request().getClientId();
			if (clientId != null && clientId.equals(tokenRequest.getClientId())) {
				if (this.clientDetailsService != null) {
					try {
						this.clientDetailsService.loadClientByClientId(clientId);
					} catch (ClientRegistrationException e) {
						log.debug("Invalid client credentials");
						throw new InvalidTokenException("Client not valid: " + clientId, e);
					}
				}

				Set<String> scope = tokenRequest.getScope();
				OAuth2Request clientAuth = authentication.getOAuth2Request().refresh(tokenRequest);
				if (scope != null && !scope.isEmpty()) {
					Set<String> originalScope = clientAuth.getScope();
					if (originalScope == null || !originalScope.containsAll(scope)) {
						log.debug("Unable to narrow the scope of the client authentication to {}", scope);
						throw new InvalidScopeException(
								"Unable to narrow the scope of the client authentication to " + scope + ".",
								originalScope);
					}

					clientAuth = clientAuth.narrowScope(scope);
				}
				return new OAuth2Authentication(clientAuth, userAuth);
			} else {
				log.debug("Client is missing or does not correspond to the MFA token");
				throw new InvalidCustomGrantException("Client is missing or does not correspond to the MFA token");
			}
		}
		return null;
	}
}