package in.nic.ashwini.eForms.exceptions.custom;

public class ProfileNotCreated extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String message;

	public ProfileNotCreated() {
		super();
		this.message = "";
	}

	public ProfileNotCreated(String message) {
		super();
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
