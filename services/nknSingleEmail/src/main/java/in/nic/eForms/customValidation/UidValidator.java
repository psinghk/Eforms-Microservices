package in.nic.eForms.customValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UidValidator implements ConstraintValidator<Uid, String> {
	@Override
	public boolean isValid(String uid, ConstraintValidatorContext context) {
		//String[] words = uid.split("\\s+");
        //System.out.println("words::::::::::::="+words);
		
		if (uid != null) {
			if (!uid.isEmpty()) {
				if (!uid.matches(
						"((^\\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\\s*$)|(^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$))")) {
					String[] words = uid.split("\\s+");
					if (words.length > 1) {
			        	System.out.println("::::::9::::::");
			            //errorMsg = "Userid cannot contain whitespaces.";
			        }
				
				} else {
					return true;
				}
			}else if (Character.isDigit(uid.charAt(0))) {
				//errorMsg = "Userid can not start with a numeric value.";
				return false;
			} if (uid.contains("_")) {
	        	System.out.println("::::::3::::::");
	           // errorMsg = "Userid can not contain underscore[_].";
	            System.out.println("::::::4::::::");
	            return false;
	        } 
	        if (uid.endsWith(".") || uid.endsWith("-") || uid.startsWith(".") || uid.startsWith("-")) {
	        	System.out.println("::::::5::::::");
	            //errorMsg = "Userid can not start or end with dot[.] and hyphen[-]. ";
	        	return false;
	        } 
	        if (uid.contains("..") || uid.contains("--")) {
	        	System.out.println("::::::6::::::");
	            //errorMsg = "Userid can not contain continuous dot[.] or hyphen[-].";
	        	return false;
	        }  
	        if (uid.length() < 8 || uid.length() > 20) {
	        	System.out.println("::::::7::::::");
	            //errorMsg = "Userid can not be less than 8 characters or more than 20 characters.";
	        	return false;
	        } 
	        if (!uid.matches("^[\\w\\-\\.\\+]+$")) {
	        	System.out.println("::::::8::::::");
	            //errorMsg = "Userid is not in correct format.";
	        	return false;
	        }  
//	        
	        
	        if (!uid.contains(".") && !uid.contains("-")) {
            	System.out.println("::::::10::::::");
                //errorMsg = "Userid must contain dot[.] or hyphen[-]";
            	
            }

			
			else {
				System.out.println("::::::11::::::");
				return true;
			}
		} else {
			System.out.println("::::::12::::::");
			return true;
		}
		return false;

	}
}
