package in.nic.ashwini.eForms.exceptions.custom;

import lombok.Data;

@Data
public class NoRecordFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	private String attribute;

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

	public NoRecordFoundException(String attribute, String message) {
		super();
		this.message = message;
		this.attribute = attribute;
	}

}
