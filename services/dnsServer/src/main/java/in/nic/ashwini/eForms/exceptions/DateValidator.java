package in.nic.ashwini.eForms.exceptions;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<IsWithinAMonth, LocalDate> {
	@Override
	public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
		
        Date migrationDate = java.sql.Date.valueOf(value);

        Calendar cheque = Calendar.getInstance();
        Calendar future = Calendar.getInstance();

        cheque.setTime(migrationDate);
        future.add(Calendar.MONTH, 1);
        //past.add(Calendar.MONTH, -3);
        
        return !(cheque.after(future));
        
	}
}
