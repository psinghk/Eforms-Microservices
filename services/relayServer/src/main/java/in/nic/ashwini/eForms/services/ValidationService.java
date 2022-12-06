package in.nic.ashwini.eForms.services;

import org.springframework.stereotype.Component;

@Component
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
	
	public boolean serviceipValidation(String serviceip) {
        boolean flag = false;
        if (!serviceip.isEmpty()) {
            if (!serviceip.matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) {
                flag = true;
            } else if (serviceip.startsWith("0") || serviceip.equals("0.0.0.0") || serviceip.equals("127.0.0.1") || serviceip.equals("255.255.255.255") || serviceip.endsWith("255")) {
                flag = true;
            }
        }
        return flag;
    }

	public boolean baseipValidation(String appIp) {
		boolean flag = false;
        if (appIp == null || appIp.isEmpty()) {
            flag = true;
        } else if (!appIp.matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) {
            flag = true;
        } else if (appIp.startsWith("0") || appIp.equals("0.0.0.0") || appIp.equals("127.0.0.1") || appIp.equals("255.255.255.255") || appIp.endsWith("255")) {
            flag = true;
        }
        return flag;
	}
	
	   public boolean relayUrlValidation(String port) {

	        boolean flag = false;
	        if (!port.isEmpty()) {
	            if (port.matches("^(?:(?:(?:https?|ftp):)?\\/\\/)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})).?)(?::\\d{2,5})?(?:[/?#]\\S*)?$")) {
	                flag = false;
	            } else if (port.matches("^((http:\\/\\/|https:\\/\\/)([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\/[a-z]{1,})*$")) {
	                String ip = port.substring(port.indexOf("//") + 2, port.length());
	                if (ip.startsWith("0") || ip.equals("0.0.0.0") || ip.equals("127.0.0.1") || ip.equals("255.255.255.255") || ip.endsWith("255")) {
	                    flag = true;
	                } else {
	                    flag = false;
	                }
	            } else {
	                flag = true;
	            }
	        }
	        return flag;
	    }
	   
	   public boolean EmailValidation(String email) {
	        boolean flag = false;
	        if (email.isEmpty()) {
	            flag = true;
	        } else if (!email.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) {
	            flag = true;
	        }
	        return flag;
	    }
	
	
}
