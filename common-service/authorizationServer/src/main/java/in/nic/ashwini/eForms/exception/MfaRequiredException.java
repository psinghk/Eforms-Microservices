package in.nic.ashwini.eForms.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

public class MfaRequiredException extends OAuth2Exception {

	private static final long serialVersionUID = 1L;

	public MfaRequiredException(String mfaToken, String messageForMobileOtp, String messageForEmailOtp) {
		super("Multi-factor authentication required");
		this.addAdditionalInformation("mfa_token", mfaToken);
		if (!messageForEmailOtp.isEmpty())
			this.addAdditionalInformation("messageForEmailOtp", messageForEmailOtp);
		if (!messageForMobileOtp.isEmpty())
			this.addAdditionalInformation("messageForMobileOtp", messageForMobileOtp);
	}

	public String getOAuth2ErrorCode() {
		return "mfa_required";
	}

	public int getHttpErrorCode() {
		return 200;
	}
}
