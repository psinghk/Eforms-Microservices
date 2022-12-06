package in.nic.eForms.customValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;



public class PrimaryIdVaidator implements ConstraintValidator<PrimaryIdValid, String> {
	@Override
	public boolean isValid(String primaryid, ConstraintValidatorContext context) {
		
		if (primaryid != null) {
			if (!primaryid.isEmpty()) {
				if (!primaryid.matches("^[a-zA-Z0-9\\\\\\\\.\\\\\\\\-]*$")) {
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
