package in.nic.eForms.customValidation;



import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;



import in.nic.eForms.utils.Util;

class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
	 
	private final Util utilityService;
 
    public UniqueEmailValidator(Util utilityService) {
        this.utilityService = utilityService;
    }
 
    public void initialize(UniqueEmail constraint) {
    }
 
    public boolean isValid(String login, ConstraintValidatorContext context) {
    	if(utilityService.allLdapValues(login)==null) {
		return true;
    	}else {
    	return false;
    	}
      
    }
 
}
