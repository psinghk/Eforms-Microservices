package in.nic.ashwini.eForms.security.granters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.BadCredentialsException;
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

import in.nic.ashwini.eForms.exception.CustomAuthException;
import in.nic.ashwini.eForms.exception.GenericException;
import in.nic.ashwini.eForms.exception.InvalidCustomGrantException;
import in.nic.ashwini.eForms.service.EncryptionService;
import in.nic.ashwini.eForms.service.MfaService;
import in.nic.ashwini.eForms.service.UtilityService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NonGovMfaTokenGranter extends AbstractTokenGranter {
	private static final String GRANT_TYPE = "non_gov_mfa";
	private final TokenStore tokenStore;
	private final ClientDetailsService clientDetailsService;
	private final MfaService mfaService;
	private final UtilityService utilityService;

	public NonGovMfaTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer, MfaService mfaService, UtilityService utilityService) {
		super(endpointsConfigurer.getTokenServices(), endpointsConfigurer.getClientDetailsService(),
				endpointsConfigurer.getOAuth2RequestFactory(), GRANT_TYPE);
		this.tokenStore = endpointsConfigurer.getTokenStore();
		this.clientDetailsService = endpointsConfigurer.getClientDetailsService();
		this.mfaService = mfaService;
		this.utilityService = utilityService;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		final String mfaToken = parameters.get("mfa_token");
		int codeOnEmail = -1, codeOnMobile = -1;
		boolean emailCodeVerified = false;
		boolean mobileCodeVerified = false;
		if (mfaToken != null) {
			OAuth2Authentication authentication = loadAuthentication(mfaToken);
			String username = EncryptionService.decrypt(authentication.getName());
			String[] decryptedUsername = username.split(";");
			log.info("USER : {}", decryptedUsername[0]);
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			if (authorities.isEmpty() && !authorities.contains(new SimpleGrantedAuthority("ROLE_NONGOV_USER"))) {
				log.debug("You are not authorized for this grant!!!");
				throw new InvalidCustomGrantException("You are not authorized for this grant!!!");
			}

			if (!parameters.containsKey("mfa_code_email") && !parameters.containsKey("mfa_code_mobile")) {
				log.debug("Missing MFA code!!!");
				throw new GenericException("Missing MFA code!!!");
			}

			if (authorities.contains(new SimpleGrantedAuthority("ROLE_OLD_USER"))) {
				if (!parameters.containsKey("mfa_code_email")) {
					log.debug("Missing OTP sent on email address!!!");
					throw new GenericException("Missing OTP sent on email address!!!");
				}
				if (!parameters.containsKey("mfa_code_mobile")) {
					log.debug("Missing OTP sent on mobile phone!!!");
					throw new GenericException("Missing OTP sent on mobile phone!!!");
				}

				codeOnEmail = parseCode(parameters.get("mfa_code_email"));
				codeOnMobile = parseCode(parameters.get("mfa_code_mobile"));
				emailCodeVerified = mfaService.verifyCode(false, decryptedUsername[0], decryptedUsername[1],
						codeOnEmail);
				mobileCodeVerified = mfaService.verifyCode(true, decryptedUsername[0], decryptedUsername[1],
						codeOnMobile);

				if (emailCodeVerified && mobileCodeVerified) {
					log.info("OTP VERIFICATION SUCCESSFULL");
					return getAuthentication(tokenRequest, authentication, decryptedUsername[0], emailCodeVerified,
							mobileCodeVerified);
				}
				log.debug("OTP verification failed!!!");
				throw new BadCredentialsException("Code verification failed!!!");
			} else {
				if (parameters.containsKey("mfa_code_email") || parameters.containsKey("mfa_code_mobile")) {

					if (parameters.containsKey("mfa_code_email")) {
						codeOnEmail = parseCode(parameters.get("mfa_code_email"));
						emailCodeVerified = mfaService.verifyCode(false, decryptedUsername[0], decryptedUsername[1],
								codeOnEmail);
					}
					if (parameters.containsKey("mfa_code_mobile")) {
						codeOnMobile = parseCode(parameters.get("mfa_code_mobile"));
						mobileCodeVerified = mfaService.verifyCode(true, decryptedUsername[0], decryptedUsername[1],
								codeOnMobile);
					}
					if (emailCodeVerified || mobileCodeVerified) {
						log.info("OTP VERIFICATION SUCCESSFULL");
						return getAuthentication(tokenRequest, authentication, decryptedUsername[0], emailCodeVerified,
								mobileCodeVerified);
					}
					log.debug("OTP verification failed!!!");
					throw new CustomAuthException("Code verification failed!!!");
				}
				log.debug("Missing MFA code. Either email otp or mobile otp is required!!!");
				throw new GenericException("Missing MFA code. Either email otp or mobile otp is required!!!");
			}
		}
		log.debug("Missing MFA token");
		throw new GenericException("Missing MFA token");
	}

	private OAuth2Authentication loadAuthentication(String accessTokenValue) {
		log.debug("Fetching OAuth2Authentication from token!!!");
		OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(accessTokenValue);

		if (accessToken == null) {
			log.debug("Invalid access token!!!");
			throw new GenericException("Invalid access token: " + accessTokenValue);
		}
		if (accessToken.isExpired()) {
			this.tokenStore.removeAccessToken(accessToken);
			log.debug("Access token expired!!!");
			throw new GenericException("Access token expired: " + accessTokenValue);
		}
		OAuth2Authentication result = this.tokenStore.readAuthentication(accessToken);
		if (result == null) {
			log.debug("Invalid access token!!!");
			throw new GenericException("Invalid access token: " + accessTokenValue);
		}
		return result;
	}

	private int parseCode(String codeString) {
		log.debug("Converting OTP from String to integer");
		try {
			return Integer.parseInt(codeString);
		} catch (NumberFormatException e) {
			log.debug("String to integer parsing exception occurred !!!");
			throw new GenericException("Invalid MFA code");
		}
	}

	private OAuth2Authentication getAuthentication(TokenRequest tokenRequest, OAuth2Authentication authentication,
			String email, boolean emailCodeVerified, boolean mobileCodeVerified) {
		log.debug("Authenticating the user and client credentials fetched through token!!!");
		List<GrantedAuthority> gaList = new ArrayList<>();
		if (emailCodeVerified || mobileCodeVerified) {
			gaList.add(new SimpleGrantedAuthority("ROLE_FULLY_AUTHENTICATED"));
			if (!emailCodeVerified) {
				gaList.add(new SimpleGrantedAuthority("ROLE_EMAIL_OTP_VERIFICATION_REQUIRED"));
			}
			if (!mobileCodeVerified) {
				gaList.add(new SimpleGrantedAuthority("ROLE_MOBILE_OTP_VERIFICATION_REQUIRED"));
			}
		}

		if (utilityService.isUserRegistered(email)) {
			gaList.add(new SimpleGrantedAuthority("ROLE_OLD_USER"));
		} else {
			gaList.add(new SimpleGrantedAuthority("ROLE_NEW_USER"));
		}

		gaList.add(new SimpleGrantedAuthority("ROLE_NONGOV_USER"));
		if (utilityService.isUserRo(email)) {
			gaList.add(new SimpleGrantedAuthority("ROLE_RO"));
		}

		Authentication userAuth = new UsernamePasswordAuthenticationToken(email, "", gaList);
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
		}
		log.debug("Client is missing or does not correspond to the MFA token");
		throw new InvalidCustomGrantException("Client is missing or does not correspond to the MFA token");
	}

	private OAuth2Authentication refreshAuthentication(OAuth2Authentication authentication, TokenRequest request) {
		Set<String> scope = request.getScope();
		OAuth2Request clientAuth = authentication.getOAuth2Request().refresh(request);
		if (scope != null && !scope.isEmpty()) {
			Set<String> originalScope = clientAuth.getScope();
			if (originalScope == null || !originalScope.containsAll(scope)) {
				log.debug("Unable to narrow the scope of the client authentication to {}", scope);
				throw new InvalidScopeException(
						"Unable to narrow the scope of the client authentication to " + scope + ".", originalScope);
			}

			clientAuth = clientAuth.narrowScope(scope);
		}
		return new OAuth2Authentication(clientAuth, authentication.getUserAuthentication());
	}
}