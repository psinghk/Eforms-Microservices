package in.nic.ashwini.eForms.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

public class InvalidAppException extends OAuth2Exception {

	private static final long serialVersionUID = 1L;

	public InvalidAppException(String message) {
		super(message);
		this.addAdditionalInformation("message", message);
	}

	public String getOAuth2ErrorCode() {
		return "invalid_app";
	}

	public int getHttpErrorCode() {
		return 200;
	}
}
