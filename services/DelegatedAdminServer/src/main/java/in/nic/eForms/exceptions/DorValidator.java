package in.nic.eForms.exceptions;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.nic.eForms.models.PreviewFormBean;

public class DorValidator implements ConstraintValidator<DorValid, PreviewFormBean> {
	@Override
	public boolean isValid(PreviewFormBean previewFormBean, ConstraintValidatorContext context) {
		
		
		Date dobdate = java.sql.Date.valueOf(previewFormBean.getType());
		Date dordate = java.sql.Date.valueOf(previewFormBean.getType());
		
		Calendar dob = Calendar.getInstance();
		Calendar dor = Calendar.getInstance();
		Calendar future = Calendar.getInstance();
        Calendar past = Calendar.getInstance();
        
		dob.setTime(dobdate);
		future.setTime(dobdate);
		past.setTime(dobdate);
		System.out.println("dob::::::"+dob);
		
		dor.setTime(dordate);
		System.out.println("dor::::::"+dor);
		
		future.add(Calendar.YEAR, 67);
		System.out.println("future::::::"+future);
		
		past.add(Calendar.YEAR, 18);
		System.out.println("past::::::"+past);
		
		if((dor.equals(future)) || (dor.before(past)) || (dor.after(future)) || (dor.equals(past))){
			return false;
		}
		
		return false;
	}
}
