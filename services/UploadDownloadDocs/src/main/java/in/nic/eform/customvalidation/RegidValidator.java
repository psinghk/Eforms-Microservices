package in.nic.eform.customvalidation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class RegidValidator implements ConstraintValidator<Regid, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		System.out.println("Regid Validator");
		Pattern pattern = Pattern.compile("[a-zA-Z]+-[a-zA-Z]+[0-9]+");
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