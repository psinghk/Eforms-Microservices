package in.nic.ashwini.eForms.security.granters;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

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

import in.nic.ashwini.eForms.exception.GenericException;
import in.nic.ashwini.eForms.exception.InvalidCustomGrantException;
import in.nic.ashwini.eForms.exception.MfaRequiredException;
import in.nic.ashwini.eForms.service.EncryptionService;
import in.nic.ashwini.eForms.service.MfaService;
import in.nic.ashwini.eForms.service.UtilityService;
import in.nic.ashwini.eForms.service.ValidationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NonGovTokenGranter extends AbstractTokenGranter {
	private static final String GRANT_TYPE = "non_gov";
	private final UtilityService utilityService;
	private final ValidationService validationService;
	private final MfaService mfaService;
	
	public NonGovTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,
			UtilityService utilityService, ValidationService validationService, MfaService mfaService) {
		super(endpointsConfigurer.getTokenServices(), endpointsConfigurer.getClientDetailsService(),
				endpointsConfigurer.getOAuth2RequestFactory(), GRANT_TYPE);
		this.utilityService = utilityService;
		this.validationService = validationService;
		this.mfaService = mfaService;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		boolean otpGeneratedForMobile = false, otpGeneratedForEmail = false;
		String messageForMobileOtp = "", messageForEmailOtp = "";
		boolean isUserRegistered = false;
		if (!parameters.containsKey("username")) {
			log.debug("Missing username parameter!!!");
			throw new GenericException("Missing username parameter!!!");
		}
		if (parameters.get("username") == null || parameters.get("username").isEmpty()) {
			log.debug("Usename can not be empty!!!");
			throw new GenericException("Usename can not be empty!!!");
		}
		String email = parameters.get("username");
		if (!validationService.isFormatValid("email", email)) {
			log.debug("Invalid email format!!!");
			throw new GenericException("Invalid email format!!!");
		}
		String mobile = "";
		Authentication userAuth = null;
		Collection<GrantedAuthority> authorities = null;
		
		OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);

		if (utilityService.isGovEmployee(email)) {
			log.debug("Invalid grant for government users!!!");
			throw new InvalidCustomGrantException("Invalid grant for government users!!!");
		}
		isUserRegistered = utilityService.isUserRegistered(email);
		if (!isUserRegistered) {
			if (!parameters.containsKey("mobile")) {
				log.debug("Missing mobile parameter!!!");
				throw new GenericException("Missing mobile parameter!!!");
			}
			mobile = parameters.get("mobile");
			if (mobile == null || (mobile != null && mobile.isEmpty())) {
				log.debug("mobile can not be empty!!!");
				throw new GenericException("mobile can not be empty!!!");
			}
			mobile = mobile.trim();
			if(!validationService.isFormatValid("mobile", mobile)) {
				log.debug("Invalid mobile number!!!");
				throw new GenericException("Invalid mobile number!!!");
			}
			if (utilityService.isMobileAvailableInLdap(mobile)) {
				log.debug("You have already an email address in our NIC repository. Please login with Government email address!!!");
				throw new InvalidCustomGrantException(
						"You have already an email address in our NIC repository. Please login with Government email address!!!");
			}
			
			if (utilityService.isMobileRegisteredInEforms(mobile)) {
				log.debug("You are already registered in eForms with this mobile number. Use the email address through which you had registered.!!!");
				throw new InvalidCustomGrantException(
						"You are already registered in eForms with this mobile number. Use the email address through which you had registered.!!!");
			}
		} else {
			
			mobile = utilityService.fetchMobileFromProfile(email).trim();
			
			if (mobile == null || (mobile != null && mobile.isEmpty())) {
				log.debug("mobile can not be empty!!!");
				throw new GenericException("mobile can not be empty!!!");
			}
			if(!validationService.isFormatValid("mobile", mobile)) {
				log.debug("Invalid mobile number!!!");
				throw new GenericException("Invalid mobile number!!!");
			}
		}
		
		// Send OTP on email and SMS
		if(!mfaService.isMobileOtpActive(mobile)) {
			mfaService.generateMobileOtp(mobile);
			otpGeneratedForMobile = true;
		}
		
		if(!mfaService.isEmailOtpActive(email)) {
			mfaService.generateEmailOtp(email);
			otpGeneratedForEmail = true;
		}
		
		String[] emailArray = email.split("@");
		
		if(otpGeneratedForEmail) {
			try {
				messageForEmailOtp = "Please enter the OTP sent to "+utilityService.maskString(emailArray[0], 4, emailArray[0].length()-3, '*')+"@"+emailArray[1];
			} catch (Exception e) {
				messageForEmailOtp = "Please enter the OTP sent to entered email address.";
			}
		}else {
			try {
				messageForEmailOtp = "Old otp sent on "+ utilityService.maskString(emailArray[0], 4, emailArray[0].length()-3, '*')+"@"+emailArray[1]+" is still valid. Please enter the OTP to proceed.";
			} catch (Exception e) {
				messageForEmailOtp = "Old OTP sent on entered email address is still valid. Please use that to proceed";
			}
		}
		
		if(otpGeneratedForMobile) {
			try {
				messageForMobileOtp = "Please enter the OTP sent to "+ utilityService.maskString(mobile, 4, mobile.length()-3, '*');
			} catch (Exception e) {
				messageForMobileOtp = "Please enter the OTP sent to entered/registered mobile number";
			}
		}else {
			try {
				messageForMobileOtp = "Old otp sent on "+ utilityService.maskString(mobile, 4, mobile.length()-3, '*') +" is still valid. Please enter the OTP to proceed.";
			} catch (Exception e) {
				messageForMobileOtp = "Old OTP sent on entered/registered mobile number is still valid. Please use that to proceed";
			}
		}
		
		if (!isUserRegistered) {
			authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_PRE_AUTH"),
					new SimpleGrantedAuthority("ROLE_NONGOV_USER"));
		}else {
			authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_PRE_AUTH"),
					new SimpleGrantedAuthority("ROLE_NONGOV_USER"),
					new SimpleGrantedAuthority("ROLE_OLD_USER"));
		}
		
		userAuth = new UsernamePasswordAuthenticationToken(EncryptionService.encrypt(email + ";" + mobile), "",
				authorities);
		OAuth2AccessToken accessToken = getTokenServices()
				.createAccessToken(new OAuth2Authentication(storedOAuth2Request, userAuth));
		throw new MfaRequiredException(accessToken.getValue(), messageForMobileOtp, messageForEmailOtp);
	}
}
