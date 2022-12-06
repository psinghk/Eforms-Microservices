package in.nic.ashwini.eForms.custumvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<NameValid, String>  {

	@Override
	public boolean isValid(String name, ConstraintValidatorContext context) {
		
		if (name != null) {
			if (!name.isEmpty()) {
				if (!name.matches("^[a-zA-Z0-9 .,]{1,50}$")) {
					return false;
			        }else {
						return true;
					}
				} else {
					return true;
				}
			}
		return false;
	}
}
