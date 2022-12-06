package in.nic.ashwini.eForms.security.granters;

import java.util.Arrays;
import java.util.Base64;
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
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;

import in.nic.ashwini.eForms.exception.CustomAuthException;
import in.nic.ashwini.eForms.exception.GenericException;
import in.nic.ashwini.eForms.exception.InvalidCustomGrantException;
import in.nic.ashwini.eForms.exception.MfaRequiredException;
import in.nic.ashwini.eForms.service.MfaService;
import in.nic.ashwini.eForms.service.UtilityService;
import in.nic.ashwini.eForms.service.ValidationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DaTokenGranter extends AbstractTokenGranter {
	private static final String GRANT_TYPE = "da";
	private final AuthenticationManager authenticationManager;
	private final MfaService mfaService;
	private final UtilityService utilityService;
	private final ValidationService validationService;

	public DaTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,
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

		if (!username.endsWith("-admin")) {
			log.debug("Invalid username for admin id");
			throw new CustomAuthException("Please enter the valid username!!!");
		}

		if (!parameters.containsKey("password")) {
			log.debug("Missing password!!!");
			throw new GenericException("Missing password!!!");
		}

		String password = parameters.get("password");
		if (password == null || (password != null && password.isEmpty())) {
			log.debug("Password is empty!!!");
			throw new CustomAuthException("Password can not be empty!!!");
		}
		parameters.remove("password");
		Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);

		try {
			userAuth = this.authenticationManager.authenticate(userAuth);
		} catch (AccountStatusException | BadCredentialsException e) {
			throw new CustomAuthException(e.getMessage());
		}

		if (userAuth != null && userAuth.isAuthenticated()) {
			OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
			Collection<GrantedAuthority> authorities = null;
			boolean otpGenerated = false;
			String message = "";
			String mobile = utilityService.fetchMobile(username).trim();
			String base64EncodedMobileNumbers = Base64.getEncoder().encodeToString(mobile.getBytes());
			
			if(!mobile.contains(",")) {
				authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_PRE_AUTH"), new SimpleGrantedAuthority("ROLE_DA_PRE_AUTH"));
				mobile = utilityService.transformMobile(mobile);
				if (!mfaService.isMobileOtpActive(mobile)) {
					mfaService.generateMobileOtp(mobile);
					otpGenerated = true;
				}
				
				if (otpGenerated) {
					try {
						message = "Please enter the OTP sent to "
								+ utilityService.maskString(mobile, 4, mobile.length() - 3, '*');
					} catch (Exception e) {
						message = "Please enter the OTP sent to registered mobile number";
					}
				} else {
					try {
						message = "Old otp sent on " + utilityService.maskString(mobile, 4, mobile.length() - 3, '*')
								+ " is still valid. Please enter the OTP to proceed.";
					} catch (Exception e) {
						message = "Old OTP sent on registered mobile number is still valid. Please use that to proceed.";
					}
				}
			}else {
				authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_PRE_AUTH"), new SimpleGrantedAuthority("ROLE_DA_PRE_AUTH"), new SimpleGrantedAuthority("ROLE_"+base64EncodedMobileNumbers));
			}
			
			userAuth = new UsernamePasswordAuthenticationToken(userAuth.getName(), "", authorities);
			
//			OAuth2AccessToken accessToken = getTokenServices()
	//				.createAccessToken(new OAuth2Authentication(storedOAuth2Request, userAuth));
			OAuth2AccessToken accessToken = getTokenServices()
					.createAccessToken(new OAuth2Authentication(storedOAuth2Request, userAuth));

			throw new MfaRequiredException(accessToken.getValue(), message, "");
		}
		throw new InvalidCustomGrantException("Could not authenticate user: " + username);
	}
}
