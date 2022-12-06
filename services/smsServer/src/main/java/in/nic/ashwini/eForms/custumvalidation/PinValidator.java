package in.nic.ashwini.eForms.custumvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class PinValidator implements ConstraintValidator<PinValid, String> {

	@Override
	public boolean isValid(String pin, ConstraintValidatorContext context) {
		
		if (pin != null) {
			if (!pin.isEmpty()) {
				if (!pin.matches("^[0-9]{6}$")) {
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
