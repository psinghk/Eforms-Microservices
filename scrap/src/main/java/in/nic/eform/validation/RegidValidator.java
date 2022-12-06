package in.nic.eform.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class RegidValidator implements ConstraintValidator<Regid, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		System.out.println("Regid Validator");
		Pattern pattern = Pattern.compile("[A-Z]+-[A-Z]+[0-9]+");
		Matcher matcher = pattern.matcher(value);
		try {
			if (!matcher.matches()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}
}