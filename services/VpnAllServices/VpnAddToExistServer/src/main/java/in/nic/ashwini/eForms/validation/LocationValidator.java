package in.nic.ashwini.eForms.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LocationValidator implements ConstraintValidator<Location, String> {

	@Override
	public boolean isValid(String location, ConstraintValidatorContext context) {
		boolean flag = false;
		if (location.isEmpty()) {
			
		} else if (!location.matches("^[a-zA-Z#0-9\\s,.\\-\\/\\(\\)]{2,100}$")) {
			flag = true;
		}
		return flag;

	}

}
