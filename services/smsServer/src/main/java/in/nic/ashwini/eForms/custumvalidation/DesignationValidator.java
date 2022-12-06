package in.nic.ashwini.eForms.custumvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DesignationValidator implements ConstraintValidator<DesignationValid, String>{

	@Override
	public boolean isValid(String design, ConstraintValidatorContext context) {
		
		if (design != null) {
			if (!design.isEmpty()) {
				if (!design.matches("^[a-zA-Z .,]{1,50}$")) {
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
