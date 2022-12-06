package in.nic.ashwini.eForms.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

public class BlockRelatedException extends OAuth2Exception {

	private static final long serialVersionUID = 1L;

	public BlockRelatedException() {
		super("You have been blocked.");
		this.addAdditionalInformation("message", " Multiple hits are being observed.");
	}

	public String getOAuth2ErrorCode() {
		return "blocked_user";
	}

	public int getHttpErrorCode() {
		return 200;
	}
}
