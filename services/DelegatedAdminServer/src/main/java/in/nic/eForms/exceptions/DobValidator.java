package in.nic.eForms.exceptions;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DobValidator implements ConstraintValidator<DobValid, LocalDate> {
	@Override
	public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
		
        Date dobDate = java.sql.Date.valueOf(value);

        Calendar cheque = Calendar.getInstance();
        Calendar future = Calendar.getInstance();
        Calendar past = Calendar.getInstance();

        cheque.setTime(dobDate);
        future.setTime(dobDate);
        future.add(Calendar.YEAR, 67);
        past.setTime(dobDate);
        past.add(Calendar.YEAR, 18);
        return (!(cheque.after(future)) && (cheque.before(past)));
        
	}
}
