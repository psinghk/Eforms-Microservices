package in.nic.eForms.customValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailValid, String> {
	@Override
	public boolean isValid(String mail, ConstraintValidatorContext context) {
		
	    if (mail.contains("@")) {
	    	 String[] words = mail.split("\\s+");
            String[] mle = mail.split("@");
            System.out.println("mleeeeeeeeeeeee" + mle);
            if (mle[0] != null) {
                System.out.println("mle000000000000" + mle[0] + "mle1" + mle[1]);
                String uidGenerated = mle[0];
                String domainGenerated = mle[1];
                uidGenerated = uidGenerated.toLowerCase().trim();
                domainGenerated = domainGenerated.toLowerCase().trim();
                System.out.println("DOMAIN Genrated :: " + domainGenerated);
                try {
                    if (mail == null || mail.isEmpty()) {
                    	 return false;
                        //errorMsg = "Mail can not be blank.";
                    } else if (Character.isDigit(mail.charAt(0))) {
                    	 return false;
                        //errorMsg = "Mail cannot start with a numeric value.";
                    } else if (mail.startsWith("-") || mail.startsWith(".")) {
                    	 return false;
                        //errorMsg = "Mail cannot starts with dot[.] and hyphen[-].";
                    } else if (mail.contains("_")) {
                    	 return false;
                        //errorMsg = "Mail cannot contain underscore[_].";
                    } else if (mail.endsWith(".") || mail.endsWith("-") || mail.startsWith(".") || mail.startsWith("-")) {
                    	 return false;
                       // errorMsg = "Mail can not start or end with dot[.] and hyphen[-]. ";
                    } else if (mail.contains("..") || mail.contains("--")) {
                    	 return false;
                        //errorMsg = "Mail can not contain continuous dot[.] or hyphen[-].";
                    	 //(email.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$"))
                    } else if (!mail.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) {
                    	 return false;
                        //errorMsg = "Mail is not in correct format.";
                    } else if (words.length > 1) {
                    	 return false;
                        //errorMsg = "Mail cannot contain whitespaces.";
                    } else if (!mail.contains("@")) {
                    	 return false;
                        //errorMsg = "Mail is not in correct format.";
                    }else {
                    	return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
            	 return false;
               // errorMsg = "Some error occurred.";
            }
        } else {
        	 return false;
           // errorMsg = "Mail is not in correct format.";
        }
		return false;

	}
}
