package in.nic.ashwini.eForms.security.granters;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.TokenStore;

import in.nic.ashwini.eForms.exception.MfaRequiredException;
import in.nic.ashwini.eForms.service.EncryptionService;
import in.nic.ashwini.eForms.service.MfaService;
import in.nic.ashwini.eForms.service.UtilityService;
import in.nic.ashwini.eForms.service.ValidationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SupportMfaTokenGranter extends AbstractTokenGranter {
	private static final String GRANT_TYPE = "sup";

	private final TokenStore tokenStore;
	private final ValidationService validationService;
	private final UtilityService utilityService;
	private final MfaService mfaService;

	public SupportMfaTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,
			AuthenticationManager authenticationManager, MfaService mfaService, UtilityService utilityService,
			ValidationService validationService) {
		super(endpointsConfigurer.getTokenServices(), endpointsConfigurer.getClientDetailsService(),
				endpointsConfigurer.getOAuth2RequestFactory(), GRANT_TYPE);
		this.tokenStore = endpointsConfigurer.getTokenStore();
		this.utilityService = utilityService;
		this.validationService = validationService;
		this.mfaService = mfaService;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		final String mfaToken = parameters.get("mfa_token");
		if (mfaToken != null) {
			if (mfaToken.isEmpty()) {
				throw new InvalidRequestException("MFA token can not be empty!!!");
			}
			OAuth2Authentication authentication = loadAuthentication(mfaToken);
			String username = EncryptionService.decrypt(authentication.getName());
			String[] decryptedUsername = username.split(":");
			String usernameDecrypted = decryptedUsername[0];
			log.info("USER : {}",usernameDecrypted);
			if (parameters.containsKey("mobile")) {
				boolean otpGenerated = false;
				String message = "";
				Authentication userAuth = authentication.getUserAuthentication();
				Collection<? extends GrantedAuthority> authorities = userAuth.getAuthorities();
				if (!authorities.contains(new SimpleGrantedAuthority("ROLE_SUPPORT_USER"))) {
					log.debug("You are not authorized!!!");
					throw new InvalidGrantException("You are not authorized!!!");
				}
				String mobile = parameters.get("mobile");
				if (mobile == null) {
					log.debug("Missing mobile number");
					throw new InvalidRequestException("Missing mobile number");
				}
				if (mobile.isEmpty()) {
					log.debug("Mobile number can not be empty!!!");
					throw new InvalidRequestException("Mobile number can not be empty!!!");
				}
				mobile = mobile.trim();
				mobile = utilityService.transformMobile(mobile);
				
				if (!validationService.isFormatValid("mobile", mobile)) {
					log.debug("Invalid mobile number!!!");
					throw new InvalidRequestException("Invalid mobile number!!!");
				}
				if (!utilityService.isSupportEmail(usernameDecrypted)) {
					log.debug("Invalid User!!!");
					throw new BadCredentialsException("Invalid User!!!");
				}
				/*
				 * Generate OTP and send to mobile number and also save it in database
				 */
				
				if(!mfaService.isMobileOtpActive(mobile)) {
					mfaService.generateMobileOtp(mobile);
					otpGenerated = true;
				}
				
				if(otpGenerated) {
					try {
						message = "Please enter the OTP sent to "+ utilityService.maskString(mobile, 4, mobile.length()-3, '*');
					} catch (Exception e) {
						message = "Please enter the OTP sent to entered mobile number";
					}
				}else {
					try {
						message = "Old otp sent on "+ utilityService.maskString(mobile, 4, mobile.length()-3, '*') +" is still valid. Please enter the OTP to proceed.";
					} catch (Exception e) {
						message = "Old OTP sent on entered mobile number is still valid. Please use that to proceed.";
					}
				}
				//Send SMS also

				OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
				userAuth = new UsernamePasswordAuthenticationToken(
						EncryptionService.encrypt(usernameDecrypted + ":" + mobile), "", authorities);
				OAuth2AccessToken accessToken = getTokenServices()
						.createAccessToken(new OAuth2Authentication(storedOAuth2Request, userAuth));
				throw new MfaRequiredException(accessToken.getValue(),message, "");
			}
			log.debug("Missing mobile number");
			throw new InvalidRequestException("Missing mobile number");
		}
		log.debug("Missing MFA token");
		throw new InvalidRequestException("Missing MFA token");
	}

	private OAuth2Authentication loadAuthentication(String accessTokenValue) {
		log.debug("Fetching OAuth2Authentication from token!!!");
		OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(accessTokenValue);

		if (accessToken == null) {
			log.debug("Invalid access token!!!");
			throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
		}
		if (accessToken.isExpired()) {
			this.tokenStore.removeAccessToken(accessToken);
			log.debug("Access token expired!!!");
			throw new InvalidTokenException("Access token expired: " + accessTokenValue);
		}
		OAuth2Authentication result = this.tokenStore.readAuthentication(accessToken);
		if (result == null) {
			log.debug("Invalid access token!!!");
			throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
		}
		return result;
	}
}