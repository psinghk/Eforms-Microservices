package in.nic.eForms.customValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;



public class ValueVaidator implements ConstraintValidator<ValueValid, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		
		if (value != null) {
			if (!value.isEmpty()) {
				if (!value.matches("^[a-zA-Z#0-9_\\\\\\\\s,'.\\\\\\\\-\\\\\\\\/\\\\\\\\(\\\\\\\\)]{2,150}$")) {
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
