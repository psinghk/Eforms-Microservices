package in.nic.ashwini.eForms.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PortValidator implements ConstraintValidator<Port, String> {

	@Override
	public boolean isValid(String port, ConstraintValidatorContext context) {

		boolean flag = false;
		if (port.isEmpty()) {
			flag = true;
		} else if (!port.matches("^[0-9]{1,10}$")) {
			flag = true;
		}
		return flag;

	}

}
