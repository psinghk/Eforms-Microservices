package in.nic.ashwini.eForms.custumvalidation;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
public class DomesticTraficValidator implements ConstraintValidator<DomesticTraficValid, String>{

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		
		return false;
	}

}
