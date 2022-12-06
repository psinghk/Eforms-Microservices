package in.nic.eform.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UidValidator implements ConstraintValidator<Uid, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		Pattern pattern = Pattern.compile("^[\\w\\-\\.\\+]+$");
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
