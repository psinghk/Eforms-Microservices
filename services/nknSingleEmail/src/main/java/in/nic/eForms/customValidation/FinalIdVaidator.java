package in.nic.eForms.customValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.nic.eForms.utils.Util;

public class FinalIdVaidator implements ConstraintValidator<FinalIdValid, String> {
	private final Util utilityService = null;
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		
		if (value == null || value.equals("")) {
			System.out.println("Userid can not be blank.");
			return false;
		} 
//		else if (!(value + "@nic.in").matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) // else if
//		{
//			System.out.println("Userid is Invalid.");
//			return false;
//			
//		}
//		else if (!value.matches("^[a-zA-Z0-9\\\\.\\\\-]*$")) {
//			System.out.println("Please enter valid Final ID");
//			System.out.println("Userid cannot contain whitespaces.");
//			return false;
//			
//		}
		
		else if (value.indexOf(".") == -1 && value.indexOf("-") == -1) {
			System.out.println(" must contain a dot(.) or hyphen(-) or both ");
			return false;
			
//		} else if (utilityService.isGovEmployee(value)) {
//			System.out.println(" UID " + value + " is available ");
//			return false;
			
		} else if (Character.isDigit(value.charAt(0))) {
			System.out.println("Userid can not start with a numeric value.");
			return false;
			
		} else if (value.contains("_")) {
			System.out.println(" uid contains _ ");
			System.out.println("Userid can not contain underscore[_].");
			return false;
			
		} else if (value.endsWith(".") || value.endsWith("-") || value.startsWith(".") || value.startsWith("-")) {
			System.out.println(" uid ends with .");
			System.out.println("Userid can not start or end with dot[.] and hyphen[-]. ");
			return false;
			
		}if (value.length() < 8 || value.length() > 26) {
			System.out.println(" uid length is " + value.length());
			System.out.println("Userid can not be less than 8 characters or more than 20");
			return false;
			
			// characters.";
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
				System.out.println("Userid contains continuous dot[.] or hyphen[-].");
				return false;
				
			}else {
				return true;
			}
		}
		
		//return false;
	}
}
