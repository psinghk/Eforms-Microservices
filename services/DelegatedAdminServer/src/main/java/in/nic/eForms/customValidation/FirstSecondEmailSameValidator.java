package in.nic.eForms.customValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FirstSecondEmailSameValidator implements ConstraintValidator<DomainValid, String> {
	@Override
	public boolean isValid(String domain, ConstraintValidatorContext context) {
		
		if (domain != null) {
			if (!domain.isEmpty()) {
				if (!domain.matches("^[a-zA-Z#0-9_\\\\\\\\s,'.\\\\\\\\-\\\\\\\\/\\\\\\\\(\\\\\\\\)]{2,150}$")) {
					return false;
			            //errorMsg = "Userid cannot contain whitespaces.";
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
