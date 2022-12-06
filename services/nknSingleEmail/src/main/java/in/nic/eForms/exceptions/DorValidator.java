package in.nic.eForms.exceptions;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.nic.eForms.models.PreviewFormBean;

public class DorValidator implements ConstraintValidator<DorValid, PreviewFormBean> {
	@Override
	public boolean isValid(PreviewFormBean previewFormBean, ConstraintValidatorContext context) {
		
		System.out.println("Single_dob:::::::"+previewFormBean.getSingle_dob());
		System.out.println("Single_dob:::::::"+previewFormBean.getSingle_dor());
		
		
		Date dobdate = java.sql.Date.valueOf(previewFormBean.getSingle_dob());
		Date dordate = java.sql.Date.valueOf(previewFormBean.getSingle_dor());
		
		Calendar dob = Calendar.getInstance();
		Calendar dor = Calendar.getInstance();
		Calendar future = Calendar.getInstance();
        Calendar past = Calendar.getInstance();
        
		dob.setTime(dobdate);
		future.setTime(dobdate);
		past.setTime(dobdate);
		System.out.println("dob::::::"+dob);//2000
		
		dor.setTime(dordate);
		System.out.println("dor::::::"+dor);
		
		future.add(Calendar.YEAR, 67);//2067
		System.out.println("future::::::"+future);
		
		past.add(Calendar.YEAR, 18);//2018
		System.out.println("past::::::"+past);
		
		if((dor.equals(past)) || (dor.before(past)) || (dor.after(future)) || (dor.equals(future))){
			System.out.println("not valid dorrr");
			return false;
		}else {
			System.out.println("valid dorrr");
			return true;
		}
	}
}
