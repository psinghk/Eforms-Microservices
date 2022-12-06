package in.nic.eForms.customValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.nic.ashwini.eForms.utils.Util;



public class FinalIdVaidator implements ConstraintValidator<FinalIdValid, String> {
	private final Util utilityService = null;
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		
		if (value == null || value.equals("")) {
			return false;
			// errorMsg = "Userid can not be blank.";
		} else if (!(value + "@nic.in").matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) // else if
		{
			return false;
			// errorMsg = "Userid is Invalid.";
		} else if (!value.matches("^[a-zA-Z0-9\\\\.\\\\-]*$")) {
			// errorMsg = "Please enter valid Final ID";
			return false;
			// errorMsg = "Userid cannot contain whitespaces.";
		} else if (value.indexOf(".") == -1 && value.indexOf("-") == -1) {
			return false;
			// msg += " UID " + value + " must contain a dot(.) or hyphen(-) or both ";
		} else if (utilityService.isGovEmployee(value)) {
			return false;
			// msg = " UID " + value + " is available ";
		} else if (Character.isDigit(value.charAt(0))) {
			return false;
			// errorMsg = "Userid can not start with a numeric value.";
		} else if (value.contains("_")) {
			System.out.println(" uid contains _ ");
			return false;
			// errorMsg = "Userid can not contain underscore[_].";
		} else if (value.endsWith(".") || value.endsWith("-") || value.startsWith(".") || value.startsWith("-")) {
			System.out.println(" uid ends with .");
			return false;
			// errorMsg = "Userid can not start or end with dot[.] and hyphen[-]. ";
		} else {
			System.out.println(" uid fine ");
			boolean uflag = false;
			for (int l = 0; l < value.length(); l++) {
				System.out.println(" inside for ");
				char a = value.charAt(l);
				if (a == '.') {
					System.out.println(" inside a is dot");
					char b = value.charAt(l + 1);
					char c = '-';
					if ((a == b) || (b == c)) {
						System.out.println(" inside a=b ");
						uflag = true;
						// valid = "false";
					}
				}
				if (a == '-') {
					System.out.println(" inside a is  - ");
					char b = value.charAt(l + 1);
					char c = '.';
					if ((a == b) || (b == c)) {
						System.out.println(" inside a=b ");
						uflag = true;
						// valid = "false";
					}
				}
			}
			System.out.println(" uid fine uflag is " + uflag);
			if (uflag) {
				return false;
				// errorMsg = "Userid contains continuous dot[.] or hyphen[-].";
			}
		}
		if (value.length() < 8 || value.length() > 20) {
			System.out.println(" uid length is " + value.length());
			return false;
			// errorMsg = "Userid can not be less than 8 characters or more than 20
			// characters.";
		}
		return false;
	}
}
