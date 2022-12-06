package in.nic.ashwini.eForms.custumvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class MobileValidator implements ConstraintValidator<MobileValid, String> {

	@Override
	public boolean isValid(String mobile, ConstraintValidatorContext context) {
		
		if (mobile != null) {
			if (!mobile.isEmpty()) {
				if (!mobile.matches("^[+0-9]{13}$")) {
					return false;
			        }
				else if(!mobile.matches("^[+0-9]{8,15}$") && !mobile.startsWith("+91")){
					return false;
				}
				
				else {
						return true;
					}
				} else {
					return true;
				}
			}
		return false;
	}


}
