package in.nic.ashwini.eForms.custumvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ServerLocValidator implements ConstraintValidator<ServerLocValid, String>
{

	@Override
	public boolean isValid(String mobile, ConstraintValidatorContext context) {
		
		if (mobile != null) {
			if (!mobile.isEmpty()) {
				if (!mobile.matches("^[a-zA-Z0-9 .,]{1,50}$")) {
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
