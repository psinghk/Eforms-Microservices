package in.nic.eform.Profile.exception.custom;

public class NoRecordFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String message;

	public NoRecordFoundException() {
		super();
		this.message = "";
	}

	public NoRecordFoundException(String message) {
		super();
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
