package in.nic.eform.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.nic.eform.validation.RegistrationNo;;

public class RegistrationNoValidator  implements ConstraintValidator<RegistrationNo, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		System.out.println("isValid");
		Pattern pattern = Pattern.compile("[A-Z]+-[A-Z]+[0-9]+");
		Matcher matcher = pattern.matcher(value);
		try {
			if (!matcher.matches()) {
				System.out.println("false");
				
				return false;
			} else {
				System.out.println("true");
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	


}
