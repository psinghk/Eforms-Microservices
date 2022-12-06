package in.nic.ashwini.eForms.security.granters;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;

import in.nic.ashwini.eForms.db.master.entities.ErrorLoginDetail;
import in.nic.ashwini.eForms.db.master.entities.LoginDetail;
import in.nic.ashwini.eForms.db.master.repositories.ErrorLoginDetailsRepository;
import in.nic.ashwini.eForms.db.master.repositories.LoginDetailsRepository;
import in.nic.ashwini.eForms.exception.BlockRelatedException;
import in.nic.ashwini.eForms.exception.CustomAuthException;
import in.nic.ashwini.eForms.exception.GenericException;
import in.nic.ashwini.eForms.exception.InvalidAppException;
import in.nic.ashwini.eForms.exception.MfaRequiredException;
import in.nic.ashwini.eForms.exception.TrailRelatedException;
import in.nic.ashwini.eForms.service.MfaService;
import in.nic.ashwini.eForms.service.UtilityService;
import in.nic.ashwini.eForms.service.ValidationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordTokenGranter extends AbstractTokenGranter {
	private static final String GRANT_TYPE = "password";
	private final AuthenticationManager authenticationManager;
	private final MfaService mfaService;
	private final UtilityService utilityService;
	private final ValidationService validationService;
	private final LoginDetailsRepository loginDetailsRepository;
	private final ErrorLoginDetailsRepository errorloginDetailsRepository;

	public PasswordTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,
			AuthenticationManager authenticationManager, MfaService mfaService, UtilityService utilityService,
			ValidationService validationService, LoginDetailsRepository loginDetailsRepository, ErrorLoginDetailsRepository errorloginDetailsRepository) {
		super(endpointsConfigurer.getTokenServices(), endpointsConfigurer.getClientDetailsService(),
				endpointsConfigurer.getOAuth2RequestFactory(), GRANT_TYPE);
		this.authenticationManager = authenticationManager;
		this.mfaService = mfaService;
		this.utilityService = utilityService;
		this.validationService = validationService;
		this.loginDetailsRepository = loginDetailsRepository;
		this.errorloginDetailsRepository = errorloginDetailsRepository;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		if(!parameters.containsKey("username")) {
			throw new GenericException("Missing username!!!");
		}
		String username = parameters.get("username");
		if(username == null || (username != null && username.isEmpty())) {
			log.debug("Email address is empty");
			throw new CustomAuthException("Email address can not be empty!!!");
		}
		if(!validationService.isFormatValid("email", username) || username.length()>50) {
			log.debug("Invalid Email format.!!!");
			throw new CustomAuthException("Invalid Email format.!!!");
		}
		if(!parameters.containsKey("password")) {
			log.debug("Missing password!!!");
			throw new GenericException("Missing password!!!");
		}
		String password = parameters.get("password");
		if(password == null || (password != null && password.isEmpty())) {
			log.debug("Password is empty!!!");
			throw new CustomAuthException("Password can not be empty!!!");
		}
		if(password.length()>50) {
			log.debug("Password length is greater than limit!!!");
			throw new CustomAuthException("Invalid Password length!!!");
		}
		parameters.remove("password");
		String ip = parameters.get("ip");
		String service = parameters.get("service");
		Set<String> aliases = utilityService.fetchAliasesFromLdap(username);
		List<LoginDetail> loginDetails = null;
		List<LoginDetail> finalLoginDetails = new ArrayList<LoginDetail>();
		List<LoginDetail> blockDetails = null;
		List<LoginDetail> finalBlockDetails = new ArrayList<LoginDetail>();
		List<ErrorLoginDetail> errorLoginDetails = null;
		List<ErrorLoginDetail> errorFinalLoginDetails = new ArrayList<ErrorLoginDetail>();
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime newTime = currentTime.minusHours(1l);

		for (String email : aliases) {
			blockDetails = loginDetailsRepository.findByEmailAndStatusAndLoginTimeGreaterThanEqualAndRoleOrderById(email, 3, newTime, service);
			finalBlockDetails.addAll(blockDetails);
			if (finalBlockDetails.size()>0) {
				throw new BlockRelatedException();
			}
		}

		for (String email : aliases) {
			errorLoginDetails = errorloginDetailsRepository.findByEmailAndLoginTimeGreaterThanEqualAndRoleOrderById(email, newTime, service);
			errorFinalLoginDetails.addAll(errorLoginDetails);
		}

		if(errorFinalLoginDetails.size()>6) {
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
			} else if(service.equalsIgnoreCase("delegated-admin")) {
				loginDetail.setRole(service);
			} else if(service.equalsIgnoreCase("coordinator-portal")) {
				loginDetail.setRole(service);
			} else {
				throw new InvalidAppException("Invalid App name");
			}
			loginDetail.setStatus(3);
			loginDetailsRepository.save(loginDetail);
		}

		for (String email : aliases) {
			LoginDetail loginDetail = loginDetailsRepository.findFirstByEmailAndStatusAndLoginTimeGreaterThanEqualAndRoleOrderByIdDesc(email, 3, newTime, service);
			if(loginDetail != null) {
				throw new TrailRelatedException();
			}
		}

		for (String email : aliases) {
			loginDetails = loginDetailsRepository.findByEmailAndStatusAndLoginTimeGreaterThanEqualAndRoleOrderById(email, 0, newTime, service);
			finalLoginDetails.addAll(loginDetails);
		}

		if(finalLoginDetails.size()>10) {
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
			} else if(service.equalsIgnoreCase("delegated-admin")) {
				loginDetail.setRole(service);
			} else if(service.equalsIgnoreCase("coordinator-portal")) {
				loginDetail.setRole(service);
			} else {
				throw new InvalidAppException("Invalid App name");
			}
			loginDetail.setStatus(3);
			loginDetailsRepository.save(loginDetail);
			throw new TrailRelatedException();
		}

		Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);

		try {
			userAuth = this.authenticationManager.authenticate(userAuth);
		} catch (AccountStatusException | BadCredentialsException e) {
			ErrorLoginDetail errorLoginDetail = new ErrorLoginDetail();
			errorLoginDetail.setEmail(username);
			if(ip == null) {
				ip = "";
			}
			errorLoginDetail.setIp(ip);
			if(service.equalsIgnoreCase("eforms")) {
				errorLoginDetail.setRole("eforms");
			}else if(service.equalsIgnoreCase("support")) {
				errorLoginDetail.setRole(service);
			}else if(service.equalsIgnoreCase("create-user")) {
				errorLoginDetail.setRole(service);
			} else if(service.equalsIgnoreCase("delegated-admin")) {
				errorLoginDetail.setRole(service);
			} else if(service.equalsIgnoreCase("coordinator-portal")) {
				errorLoginDetail.setRole(service);
			} else {
				throw new InvalidAppException("Invalid App name");
			}
			errorloginDetailsRepository.save(errorLoginDetail);
			throw new CustomAuthException(e.getMessage());
		}

		if (userAuth != null && userAuth.isAuthenticated()) {
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
			} else if(service.equalsIgnoreCase("delegated-admin")) {
				loginDetail.setRole(service);
			} else if(service.equalsIgnoreCase("coordinator-portal")) {
				loginDetail.setRole(service);
			} else {
				throw new InvalidAppException("Invalid App name");
			}
			loginDetail.setStatus(0);
			loginDetailsRepository.save(loginDetail);
			OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
			Collection<GrantedAuthority> authorities = null;
			boolean otpGenerated = false;
			String message = "";
			if(utilityService.isSupportEmail(username)) {
				authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_PRE_AUTH"), new SimpleGrantedAuthority("ROLE_SUPPORT_USER"));
			}else {
				authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_PRE_AUTH"));

				// Generate OTP and send SMS to mobile phone
				String mobile = utilityService.fetchMobile(username).trim();
				mobile = utilityService.transformMobile(mobile);

				if(!mfaService.isMobileOtpActive(mobile)) {
					mfaService.generateMobileOtp(mobile);
					otpGenerated = true;
				}

				if(otpGenerated) {
					try {
						message = "Please enter the OTP sent to "+ utilityService.maskString(mobile, 4, mobile.length()-3, '*');
					} catch (Exception e) {
						message = "Please enter the OTP sent to registered mobile number";
					}
				}else {
					try {
						message = "Old otp sent on "+ utilityService.maskString(mobile, 4, mobile.length()-3, '*') +" is still valid. Please enter the OTP to proceed.";
					} catch (Exception e) {
						message = "Old OTP sent on registered mobile number is still valid. Please use that to proceed.";
					}
				}

				//Send SMS also
			}

			userAuth = new UsernamePasswordAuthenticationToken(userAuth.getName(), "", authorities);
			String test = userAuth.getName();
			OAuth2AccessToken accessToken = getTokenServices().createAccessToken(new OAuth2Authentication(storedOAuth2Request, userAuth));
			throw new MfaRequiredException(accessToken.getValue(), message, "");
		}
		throw new CustomAuthException("Could not authenticate user: " + username);
	}
}
