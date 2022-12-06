package in.nic.ashwini.eForms.security.granters;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.security.oauth2.provider.token.TokenStore;

import in.nic.ashwini.eForms.db.master.entities.EmailOtp;
import in.nic.ashwini.eForms.db.master.entities.MobileOtp;
import in.nic.ashwini.eForms.db.master.repositories.EmailOtpRepository;
import in.nic.ashwini.eForms.db.master.repositories.MobileOtpRepository;
import in.nic.ashwini.eForms.db.slave.entities.EmailOtpFromSlave;
import in.nic.ashwini.eForms.db.slave.entities.MobileOtpFromSlave;
import in.nic.ashwini.eForms.db.slave.repositories.EmailOtpRepositoryToRead;
import in.nic.ashwini.eForms.db.slave.repositories.MobileOtpRepositoryToRead;
import in.nic.ashwini.eForms.exception.GenericException;
import in.nic.ashwini.eForms.exception.InvalidCustomGrantException;
import in.nic.ashwini.eForms.exception.MfaRequiredException;
import in.nic.ashwini.eForms.service.EncryptionService;
import in.nic.ashwini.eForms.service.MfaService;
import in.nic.ashwini.eForms.service.UtilityService;
import in.nic.ashwini.eForms.service.ValidationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResendOtpTokenGranter extends AbstractTokenGranter {
	private static final String GRANT_TYPE = "resend";

	private final TokenStore tokenStore;
	private final ValidationService validationService;
	private final UtilityService utilityService;
	private final MfaService mfaService;
	private final EmailOtpRepository emailOtpRepository;
	private final MobileOtpRepository mobileOtpRepository;
	private final EmailOtpRepositoryToRead emailOtpRepositoryToRead;
	private final MobileOtpRepositoryToRead mobileOtpRepositoryToRead;

	public ResendOtpTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer,
			AuthenticationManager authenticationManager, MfaService mfaService, UtilityService utilityService,
			ValidationService validationService, EmailOtpRepository emailOtpRepository,
			MobileOtpRepository mobileOtpRepository, EmailOtpRepositoryToRead emailOtpRepositoryToRead, MobileOtpRepositoryToRead mobileOtpRepositoryToRead) {
		super(endpointsConfigurer.getTokenServices(), endpointsConfigurer.getClientDetailsService(),
				endpointsConfigurer.getOAuth2RequestFactory(), GRANT_TYPE);
		this.tokenStore = endpointsConfigurer.getTokenStore();
		this.utilityService = utilityService;
		this.validationService = validationService;
		this.mfaService = mfaService;
		this.emailOtpRepository = emailOtpRepository;
		this.mobileOtpRepository = mobileOtpRepository;
		this.emailOtpRepositoryToRead = emailOtpRepositoryToRead;
		this.mobileOtpRepositoryToRead = mobileOtpRepositoryToRead;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		final String mfaToken = parameters.get("mfa_token");
		String otp = "";
		if (mfaToken != null) {
			if (mfaToken.isEmpty()) {
				log.debug("MFA token can not be empty!!!");
				throw new GenericException("MFA token can not be empty!!!");
			}

			if (!parameters.containsKey("otp_for")) {
				log.debug("otp_for is missing!!!");
				throw new GenericException("otp_for is missing!!!");
			}

			if (!parameters.get("otp_for").equals("email") && !parameters.get("otp_for").equals("mobile")) {
				log.debug("value for parameter otp_for is invalid!!!");
				throw new GenericException("Invalid value for otp_for!!!");
			}

			if (!parameters.containsKey("otp_type")) {
				log.debug("otp_type is missing!!!");
				throw new GenericException("otp_type is missing!!!");
			}

			if (!parameters.get("otp_type").equals("resend") && !parameters.get("otp_type").equals("new")) {
				log.debug("Invalid value for otp_type!!!");
				throw new GenericException("Invalid value for otp_type!!!");
			}

			OAuth2Authentication authentication = loadAuthentication(mfaToken);
			String username = EncryptionService.decrypt(authentication.getName());
			String[] decryptedUsername = username.split(";");
			String usernameDecrypted = decryptedUsername[0];
			String mobileDecrypted = decryptedUsername[1];

			String message = "";
			Authentication userAuth = authentication.getUserAuthentication();
			Collection<? extends GrantedAuthority> authorities = userAuth.getAuthorities();
			if (!authorities.contains(new SimpleGrantedAuthority("ROLE_PRE_AUTH"))) {
				throw new InvalidCustomGrantException("You are not authorized!!!");
			}

			/*
			 * Generate OTP and send to mobile number and also save it in database
			 */

			if (parameters.get("otp_type").equals("resend")) {
				if (parameters.get("otp_for").equals("email")) {
					Optional<EmailOtpFromSlave> emailOtpList = emailOtpRepositoryToRead.findTopByEmailAndGenerationTimeStampBeforeAndExpiryTimeStampAfterOrderByIdDesc(usernameDecrypted, LocalDateTime.now(), LocalDateTime.now());
					EmailOtpFromSlave emailOtp = null;
					String[] emailArray = usernameDecrypted.split("@");
					if (emailOtpList.isPresent()) {
						emailOtp = emailOtpList.orElse(null);
						if (emailOtp.getResendAttempt() < 4) {
							otp = emailOtp.getOtp().toString();
							System.out.println("OTP ::: " + otp);
							try {
								message = "OTP has been resent to "
										+ utilityService.maskString(emailArray[0], 4, emailArray[0].length() - 3, '*') + "@"
										+ emailArray[1];
							} catch (Exception e) {
								message = "OTP has been resent to email address";
							}
							emailOtpRepository.updateResendAttempt(usernameDecrypted, LocalDateTime.now());
						} else
							message = "All the resend attempts have been used.";
					}else {
						message = "OTP has already been expired.";
					}
				} else {
					Optional<MobileOtpFromSlave> mobileOtpList = mobileOtpRepositoryToRead
							.findTopByMobileAndGenerationTimeStampBeforeAndExpiryTimeStampAfterOrderByIdDesc(
									mobileDecrypted, LocalDateTime.now(), LocalDateTime.now());
					MobileOtpFromSlave mobileOtp = null;
					if (mobileOtpList.isPresent()) {
						mobileOtp = mobileOtpList.orElse(null);
						if (mobileOtp.getResendAttempt() < 4) {
							otp = mobileOtp.getOtp().toString();
							System.out.println("OTP ::: " + otp);
							try {
								message = "OTP has been resent to "
										+ utilityService.maskString(mobileDecrypted, 4, mobileDecrypted.length() - 3, '*');
							} catch (Exception e) {
								message = "OTP has been resent to mobile number";
							}
							mobileOtpRepository.updateResendAttempt(mobileDecrypted, LocalDateTime.now());
						} else
							message = "All the resend attempts have been used.";
					} else {
						message = "OTP has already been expired.";
					}
				}
			} else {
				if (parameters.get("otp_for").equals("email")) {
					EmailOtp emailOtp = mfaService.generateEmailOtp(usernameDecrypted);
					System.out.println("New OTP :: " + emailOtp.getOtp().toString());
					String[] emailArray = usernameDecrypted.split("@");
					try {
						message = "New OTP has been sent to "
								+ utilityService.maskString(emailArray[0], 4, emailArray[0].length() - 3, '*') + "@"
								+ emailArray[1];
					} catch (Exception e) {
						message = "New OTP has been sent to email address";
					}
					emailOtpRepository.updateResendAttempt(usernameDecrypted, LocalDateTime.now());
				} else {
					MobileOtp mobileOtp = mfaService.generateMobileOtp(mobileDecrypted);
					System.out.println("New Mobile OTP :: " + mobileOtp.getOtp().toString());
					try {
						message = "New OTP has been sent to "
								+ utilityService.maskString(mobileDecrypted, 4, mobileDecrypted.length() - 3, '*');
					} catch (Exception e) {
						message = "New OTP has been sent to mobile number";
					}
					mobileOtpRepository.updateResendAttempt(mobileDecrypted, LocalDateTime.now());
				}
			}

			// Send SMS also

			OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
			userAuth = new UsernamePasswordAuthenticationToken(
					EncryptionService.encrypt(usernameDecrypted + ";" + mobileDecrypted), "", authorities);
			OAuth2AccessToken accessToken = getTokenServices()
					.createAccessToken(new OAuth2Authentication(storedOAuth2Request, userAuth));
			if (parameters.get("otp_for").equals("email")) {
				throw new MfaRequiredException(accessToken.getValue(), message, "");
			} else {
				throw new MfaRequiredException(accessToken.getValue(), "", message);
			}

		}
		throw new GenericException("Missing MFA token");
	}

	private OAuth2Authentication loadAuthentication(String accessTokenValue) {
		log.debug("Fetching OAuth2Authentication from token!!!");
		OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(accessTokenValue);

		if (accessToken == null) {
			log.debug("Invalid access token");
			throw new GenericException("Invalid access token: " + accessTokenValue);
		}
		if (accessToken.isExpired()) {
			this.tokenStore.removeAccessToken(accessToken);
			log.debug("Access token expired!!!");
			throw new GenericException("Access token expired: " + accessTokenValue);
		}
		OAuth2Authentication result = this.tokenStore.readAuthentication(accessToken);
		if (result == null) {
			log.debug("Invalid access token");
			throw new GenericException("Invalid access token: " + accessTokenValue);
		}
		return result;
	}
}