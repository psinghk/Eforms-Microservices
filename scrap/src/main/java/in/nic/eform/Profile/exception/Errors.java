package in.nic.eform.Profile.exception;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Errors {

	private List<Error> errors;

	public Errors() {
		errors = new ArrayList<Error>();
	}

	@JsonIgnoreProperties(value = { "property" })
	public class Error {

		private String property;
		private String message;
		private String field;

		public Error(String property, String message, String... values) {
			super();
			this.property = property;
			this.message = message;

			for (String value : values) {
				this.message += "{" + value + "}";
			}

		}

		public Error(String property, String message, String field) {
			super();
			this.property = property;
			this.message = message;
			this.field = field;

		}

		public String getProperty() {
			return property;
		}

		public void setProperty(String property) {
			this.property = property;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		/**
		 * @return the field
		 */
		public String getField() {
			return field;
		}

		/**
		 * @param field the field to set
		 */
		public void setField(String field) {
			this.field = field;
		}

	}

	public boolean hasErrors() {
		if (!errors.isEmpty())
			return true;

		return false;
	}

	public void addAll(List<Error> error) {
		errors.addAll(error);
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}
}