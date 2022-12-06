package in.nic.ashwini.eForms.custumvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CityValidator implements ConstraintValidator<CityValid, String> {
	@Override
	public boolean isValid(String city, ConstraintValidatorContext context) {
		
		if (city != null) {
			if (!city.isEmpty()) {
				if (!city.matches("^[a-zA-Z*#0-9\\s,.\\-\\/\\(\\)]{1,100}$")) {
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
