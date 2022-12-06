package in.nic.ashwini.eForms.custumvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AddressValidator implements ConstraintValidator<AddressValid, String> {

	@Override
	public boolean isValid(String address, ConstraintValidatorContext context) {
		
		if (address != null) {
			if (!address.isEmpty()) {
				if (!address.matches("^[a-zA-Z#0-9\\s,.\\-\\/\\(\\)]{2,100}$")) {
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
