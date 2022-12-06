package in.nic.ashwini.eForms.custumvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class StateValidator implements ConstraintValidator<StateValid, String> {


	@Override
	public boolean isValid(String state, ConstraintValidatorContext context) {
		
		if (state != null) {
			if (!state.isEmpty()) {
				if (!state.matches("^[a-zA-Z0-9\\.\\-\\_ ]{1,100}$")) {
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
