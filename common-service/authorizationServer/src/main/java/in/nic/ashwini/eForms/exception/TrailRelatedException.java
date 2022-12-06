package in.nic.ashwini.eForms.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

public class TrailRelatedException extends OAuth2Exception {

	private static final long serialVersionUID = 1L;

	public TrailRelatedException() {
		super("You have already been authenticated. Multiple hits are being observed.");
		this.addAdditionalInformation("message", "Please use the mfa token already provided to you. More than 10 hits observed.");
	}

	public String getOAuth2ErrorCode() {
		return "suspicious_user";
	}

	public int getHttpErrorCode() {
		return 200;
	}
}
