package in.nic.ashwini.ldap.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class UsernameEmailValidator implements ConstraintValidator<UsernameEmail, String>{
	@Override
	  public boolean isValid(String value, ConstraintValidatorContext context) {
		 Pattern pattern = null;
		if(value.contains("@")) {
			pattern = Pattern.compile("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$");
			//pattern = Pattern.compile("^(([^<>()\\\\[\\\\]\\\\\\\\.,;:\\\\s@\\\"]+(\\\\.[^<>()\\\\[\\\\]\\\\\\\\.,;:\\\\s@\\\"]+)*)|(\\\".+\\\"))@((\\\\[[0-9]{1,3}\\\\.[0-9]{1,3}\\\\.[0-9]{1,3}\\\\.[0-9]{1,3}])|(([a-zA-Z0-9\\\\-]+\\\\.)+[a-zA-Z]{2,}))$");
		}else {
			pattern = Pattern.compile("^[\\w\\-\\.\\+]+$");
			//pattern = Pattern.compile("^(([^<>()\\\\\\\\[\\\\\\\\]\\\\\\\\\\\\\\\\.,;:\\\\\\\\s@\\\\\\\"]+(\\\\\\\\.[^<>()\\\\\\\\[\\\\\\\\]\\\\\\\\\\\\\\\\.,;:\\\\\\\\s@\\\\\\\"]+)*)|(\\\\\\\".+\\\\\\\"))$");
		}
		Matcher matcher = pattern.matcher(value);
	    try {
	      if (!matcher.matches()) {
	        return false;
	      } else {
	        return true;
	      }
	    } catch (Exception e) {
	      return false;
	    }
	  }
}
