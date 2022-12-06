package in.nic.ashwini.eForms.service;

public class ValidationService {
	
	public boolean isFormatValid(String type, String value) {
        String typeOfData = type.toLowerCase();
        boolean flag = false;
        switch (typeOfData) {
            case "email":
                flag = value.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$");
                //flag = value.matches("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z0-9\\-]+\\.)+[a-zA-Z]{2,}))$");
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
                flag = value.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$");
                break;
            case "department":
                flag = value.matches("^[+0-9]{10,13}$");
                break;
            case "employment":
                flag = value.matches("^[+0-9]{10,13}$");
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
