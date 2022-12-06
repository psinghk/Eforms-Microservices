package in.nic.ashwini.eForms.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

public class CustomAuthException extends OAuth2Exception {

	private static final long serialVersionUID = 1L;

	public CustomAuthException(String message) {
		super("Please check your username and password.");
		this.addAdditionalInformation("message", message);
	}

	public String getOAuth2ErrorCode() {
		return "bad_credentials";
	}

	public int getHttpErrorCode() {
		return 200;
	}
}
