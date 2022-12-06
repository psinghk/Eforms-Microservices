package in.nic.eform.Profile.exception.custom;

public class ProfileIsNotCreated extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String message;

	public ProfileIsNotCreated() {
		super();
		this.message = "";
	}

	public ProfileIsNotCreated(String message) {
		super();
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
