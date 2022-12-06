package in.nic.ashwini.eForms.custumvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AuditValidator implements ConstraintValidator<AuditValid, String>{

	@Override
	public boolean isValid(String audit, ConstraintValidatorContext context) {
		
		if (audit != null) {
			if (!audit.isEmpty()) {
				if (!audit.matches("^[a-zA-Z .,]{1,50}$")) {
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
