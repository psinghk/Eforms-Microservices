package in.nic.ashwini.eForms.services;

public class ValidationService {
	private static final String EMAIL_REGEX = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$";
	private static final String MINISTRY_DEPARTMENT_ORGANIZATION_REGEX = "^[a-zA-Z#0-9\\s,'.\\-\\/\\&\\_(\\)]{2,100}$";
	private static final String EMPLOYMENT_CATEGORY_REGEX = "^[a-zA-Z0-9 .,-_&]{1,50}$"; 
	
	public static boolean isFormatValid(String type, String value) {
        String typeOfData = type.toLowerCase();
        boolean flag = false;
        switch (typeOfData) {
            case "email":
                flag = value.matches(EMAIL_REGEX);
                break;
            case "mobile":
                flag = value.matches("^[+0-9]{10,13}$");
                break;
            case "title":
                flag = value.matches("^[a-zA-Z.]{2,6}$");
                break;
            case "name":
                flag = value.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$");
                break;
            case "telephone":
                flag = value.matches("^[+0-9]{10,13}$");
                break;
            case "employeecode":
                flag = value.matches("^[+0-9]{10,13}$");
                break;
            case "address":
                flag = value.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$");
                break;
            case "district":
                flag = value.matches("^[+0-9]{10,13}$");
                break;
            case "pincode":
                flag = value.matches("^[+0-9]{10,13}$");
                break;
            case "ministry":
                flag = value.matches(MINISTRY_DEPARTMENT_ORGANIZATION_REGEX);
                break;
            case "department":
                flag = value.matches(MINISTRY_DEPARTMENT_ORGANIZATION_REGEX);
                break;
            case "organization":
                flag = value.matches(MINISTRY_DEPARTMENT_ORGANIZATION_REGEX);
                break;
            case "employment":
            	//@Pattern(regexp = "^[a-zA-Z0-9 .,-_&]{1,50}$", message = "Please enter employment in correct format, Alphanumeric(,.) allowed  [limit 1-50]")
                flag = value.matches(EMPLOYMENT_CATEGORY_REGEX);
                break;
            case "state":
                flag = value.matches(MINISTRY_DEPARTMENT_ORGANIZATION_REGEX);
                break;
            case "purpose":
                flag = value.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$");
                break;
            case "url":
                flag = value.matches("^[+0-9]{10,13}$");
                break;
            case "ip":
                flag = value.matches("^[+0-9]{10,13}$");
                break;
            case "mac":
                flag = value.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$");
                break;
            case "pullkeyword":
                flag = value.matches("^[+0-9]{10,13}$");
                break;
            case "cname":
                flag = value.matches("^[+0-9]{10,13}$");
                break;    
            default:
                System.out.println("Invalid type");
        }
        return flag;
    }
}
