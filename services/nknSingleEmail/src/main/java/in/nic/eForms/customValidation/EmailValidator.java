package in.nic.eForms.customValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailValid, String> {
	@Override
	public boolean isValid(String mail, ConstraintValidatorContext context) {
		System.out.println("mail::::::::::"+mail);
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
                    	 System.out.println("Mail can not be blank.");
                    	 return false;
                    } else if (Character.isDigit(mail.charAt(0))) {
                    	 System.out.println("Mail cannot start with a numeric value.");
                    	 return false;
                    	
                    } else if (mail.startsWith("-") || mail.startsWith(".")) {
                    	 System.out.println("Mail cannot starts with dot[.] and hyphen[-].");
                    	 return false;
                    	
                    } else if (mail.contains("_")) {
                    	System.out.println("Mail cannot contain underscore[_].");
                    	 return false;
                    	 
                    } else if (mail.endsWith(".") || mail.endsWith("-") || mail.startsWith(".") || mail.startsWith("-")) {
                    	System.out.println("Mail can not start or end with dot[.] and hyphen[-]. "); 
                    	return false;
                    	 
                    } else if (mail.contains("..") || mail.contains("--")) {
                    	System.out.println("Mail can not contain continuous dot[.] or hyphen[-].");
                    	 return false;
                    	 
                    } else if (!mail.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) {
                    	 System.out.println("Mail is not in correct format.");
                    	 return false;
                    	
                    } else if (words.length > 1) {
                    	 System.out.println("Mail cannot contain whitespaces.");
                    	 return false;
                    	
                    } else if (!mail.contains("@")) {
                    	System.out.println("Mail is not in correct format.");
                    	 return false;
                    	 
                    }else {
                    	System.out.println("Mail is  in correct format.");
                    	return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
            	System.out.println("Some error occurred.");
            	 return false;
            	 
            }
        } else {
        	System.out.println("Mail is not in correct format.");
        	 return false;
        	 
        }
		return false;

	}
}
