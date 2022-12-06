package in.nic.ashwini.eForms.security.granters;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
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

import in.nic.ashwini.eForms.db.master.entities.ErrorOtpDetail;
import in.nic.ashwini.eForms.db.master.entities.LoginDetail;
import in.nic.ashwini.eForms.db.master.repositories.ErrorOtpDetailsRepository;
import in.nic.ashwini.eForms.db.master.repositories.LoginDetailsRepository;
import in.nic.ashwini.eForms.exception.BlockRelatedException;
import in.nic.ashwini.eForms.exception.CustomAuthException;
import in.nic.ashwini.eForms.exception.GenericException;
import in.nic.ashwini.eForms.exception.InvalidCustomGrantException;
import in.nic.ashwini.eForms.exception.TrailRelatedException;
import in.nic.ashwini.eForms.service.EncryptionService;
import in.nic.ashwini.eForms.service.MfaService;
import in.nic.ashwini.eForms.service.UtilityService;
import in.nic.ashwini.eForms.service.ValidationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MfaTokenGranter extends AbstractTokenGranter {
	private static final String GRANT_TYPE = "mfa";

	private final TokenStore tokenStore;
	private final ClientDetailsService clientDetailsService;
	private final AuthenticationManager authenticationManager;
	private final MfaService mfaService;
	private final UtilityService utilityService;
	private final ValidationService validationService;
	private final LoginDetailsRepository loginDetailsRepository;
	private final ErrorOtpDetailsRepository errorOtpDetailsRepository;

	public MfaTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,
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
		String ip = parameters.get("ip");
		String service = parameters.get("service");
		if(service == null) {
			service = "eforms";
		} else if(service.isEmpty()) {
			service = "eforms";
		}
		if (mfaToken != null) {
			OAuth2Authentication authentication = loadAuthentication(mfaToken);
			String username = EncryptionService.decrypt(authentication.getName());
			String usernameDecrypted = "";
			String[] decryptedUsername = null;
			if(username != null) {
				decryptedUsername = username.split(";");
				usernameDecrypted = decryptedUsername[0];
			}
			log.info("USER : {}",usernameDecrypted);
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			LocalDateTime currentTime = LocalDateTime.now();
			LocalDateTime newTime = currentTime.minusHours(1l);
			if (!authorities.isEmpty() && authorities.contains(new SimpleGrantedAuthority("ROLE_PRE_AUTH"))) {
				if (parameters.containsKey("mfa_code") && parameters.get("mfa_code").length()==6) {
					int code = parseCode(parameters.get("mfa_code"));
					log.debug("OTP Verification started!!!");

					List<LoginDetail> blockDetails = null;
					blockDetails = loginDetailsRepository.findByEmailAndStatusAndLoginTimeGreaterThanEqualAndRoleOrderById(usernameDecrypted, 3, newTime, service);
					if (blockDetails.size()>0) {
						throw new BlockRelatedException();
					}

					List<ErrorOtpDetail> errorOtpDetailsList = errorOtpDetailsRepository.findByMobileAndLoginTimeGreaterThanEqualOrderById(decryptedUsername[1], newTime);
					if(errorOtpDetailsList.size()>6) {
						LoginDetail loginDetail = new LoginDetail();
						loginDetail.setEmail(username);
						if(ip == null) {
							ip = "";
						}
						loginDetail.setIp(ip);
						if(service.equalsIgnoreCase("eforms")) {
							loginDetail.setRole("eforms");
						}else if(service.equalsIgnoreCase("support")) {
							loginDetail.setRole(service);
						}else if(service.equalsIgnoreCase("create-user")) {
							loginDetail.setRole(service);
						}else {
							throw new CustomAuthException("Invalid App name");
						}
						loginDetail.setStatus(3);
						loginDetailsRepository.save(loginDetail);
						throw new TrailRelatedException();
					}

					if (mfaService.verifyCode(true, decryptedUsername[0], decryptedUsername[1], code)) {
						log.info("OTP VERIFICATION SUCCESSFULL");
						///////////////////////////////////////
						LoginDetail loginDetail = new LoginDetail();
						loginDetail.setEmail(decryptedUsername[0]);
						if(ip == null) {
							ip = "";
						}
						loginDetail.setIp(ip);
						if(service.equalsIgnoreCase("eforms")) {
							loginDetail.setRole("eforms");
						}else if(service.equalsIgnoreCase("support")) {
							loginDetail.setRole(service);
						}else if(service.equalsIgnoreCase("create-user")) {
							loginDetail.setRole(service);
						}else {
							throw new CustomAuthException("Invalid App name");
						}
						loginDetail.setStatus(1);
						loginDetailsRepository.save(loginDetail);
						///////////////////////////////////////
						return getAuthentication(tokenRequest, authentication, ip, service, usernameDecrypted);
					}else {
						ErrorOtpDetail errorOtpDetails = new ErrorOtpDetail();
						errorOtpDetails.setEmail(decryptedUsername[0]);
						errorOtpDetails.setMobile(decryptedUsername[1]);
						if(ip == null) {
							ip = "";
						}
						errorOtpDetails.setIp(ip);
						errorOtpDetails.setInvalidOtp(String.valueOf(code));
						if(service.equalsIgnoreCase("eforms")) {
							errorOtpDetails.setRole("eforms");
						}else if(service.equalsIgnoreCase("support")) {
							errorOtpDetails.setRole(service);
						}else if(service.equalsIgnoreCase("create-user")) {
							errorOtpDetails.setRole(service);
						}else {
							throw new CustomAuthException("Invalid App name");
						}
						errorOtpDetailsRepository.save(errorOtpDetails);
					}
				} else {
					log.debug("Missing MFA code");
					throw new GenericException("Missing MFA code");
				}
				log.debug("Invalid MFA code");
				throw new GenericException("Invalid MFA code");
			}
			log.debug("You are not authorized for this grant!!!");
			throw new GenericException("You are not authorized for this grant!!!");
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

	private int parseCode(String codeString) {
		log.debug("Converting OTP from String to integer");
		try {
			return Integer.parseInt(codeString);
		} catch (NumberFormatException e) {
			log.debug("String to integer parsing exception occurred !!!");
			throw new InvalidCustomGrantException("Invalid MFA code");
		}
	}

	private OAuth2Authentication getAuthentication(TokenRequest tokenRequest, OAuth2Authentication authentication, String ip, String service, String usernameDecrypted) {
		log.debug("Authenticating the user and client credentials fetched through token!!!");
		String mobile = utilityService.fetchMobile(usernameDecrypted);
		if (mobile.contains(",")) {
			String[] arrMobile = mobile.split(",");
			mobile = arrMobile[0];
		}
		mobile = utilityService.transformMobile(mobile);
		Authentication user = new UsernamePasswordAuthenticationToken(EncryptionService.encrypt(usernameDecrypted+";"+mobile+";userName=>"+usernameDecrypted+";service=>"+service+";ip=>"+ip), "", authentication.getAuthorities());
		try {
			user = this.authenticationManager.authenticate(user);
		} catch (AccountStatusException | BadCredentialsException e) {
			throw new CustomAuthException("Invalid token!!!");
		}
		//Authentication user = this.authenticationManager.authenticate(authentication.getUserAuthentication());
		// Object details = authentication.getDetails();
		authentication = new OAuth2Authentication(authentication.getOAuth2Request(), user);
		// authentication.setDetails(details);

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