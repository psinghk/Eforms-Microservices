package in.nic.ashwini.ldap.validation;

import lombok.Data;

@Data
public class CustomException extends RuntimeException {

	/**
	 * Unique ID for Serialized object
	 */
	private static final long serialVersionUID = 4657491283614755649L;
	private String attribute;

	public CustomException(String msg) {
		super(msg);
	}

	public CustomException(String msg, Throwable t) {
		super(msg, t);
	}

	public CustomException(String attribute, String msg) {
		super(msg);
		this.attribute = attribute;
	}
	
	

}