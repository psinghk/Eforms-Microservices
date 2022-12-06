package in.nic.eForms.customValidation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.nic.eForms.utils.Util;


@Component
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String>{

	@Autowired
	private Util utilityService;
	

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		List<String> emailsAgainstMobileList = utilityService.emailsAgainstMobile(value);
		System.out.println("total email::::"+emailsAgainstMobileList);
		if(emailsAgainstMobileList.size() >= 1)
		{
			return false;
		}
		return true;
		
	}
}