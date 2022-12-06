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
        cheque.setTime(dobDate);//2000
        
        
//        Calendar maxdate = Calendar.getInstance();
//        maxdate.setTime(dobDate);
//        maxdate.add(Calendar.YEAR, 67);//
        
        
        Calendar mindate = Calendar.getInstance();
        mindate.add(Calendar.YEAR, -18);
        
        
        
        if(!cheque.equals(mindate) || !cheque.before(mindate)) {
        	System.out.println("valid dob");
         return true;
        }else {
        	return false;
        }
         
       
        
	}
}
