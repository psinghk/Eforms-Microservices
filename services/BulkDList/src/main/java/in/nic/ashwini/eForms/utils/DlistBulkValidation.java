package in.nic.ashwini.eForms.utils;

import java.util.HashMap;

public class DlistBulkValidation
{
	
	HashMap values = new HashMap();

	public String listValidation(String list1) {
        String msg = "";
        if (list1.contains("@") && list1.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) {
            String[] t = list1.split("@");
            String list = t[0];
            String listend = t[1];
            if (list.isEmpty()) {
                msg = "Please Enter List Name";
            } else if (list.length() < 6 || list.length() > 20) {
                msg = "List Name contain min length is 6, max length is 20";
            } else if (list.contains("_")) {
                msg = "List Name can't contain underscore(_)";
            } else if (list.startsWith("-") || list.startsWith(".") || list.endsWith("-") || list.endsWith(".")) {
                msg = "List Name can't start or end with hyphen(-) or dot[.]";
            } else {
                boolean uflag = false;
                for (int l = 0; l < list.length(); l++) {
                    char a = list.charAt(l);
                    if (a == '.') {
                        char b = list.charAt(l + 1);
                        char c = '-';
                        if (a == b || b == c) {
                            uflag = true;
                        }
                    }
                    if (a == '-') {
                        char b = list.charAt(l + 1);
                        char c = '.';
                        if (a == b || b == c) {
                            uflag = true;
                        }
                    }
                }
                if (uflag) {
                    msg = "Please enter List Name in correct format";
                } else if (!list.matches("^[a-zA-Z0-9.-]{6,20}$")) {
                    msg = "Please enter List Name in correct format";
                }
                if (!listend.equalsIgnoreCase("lsmgr.nic.in")) {
                    msg = "Please append @lsmgr.nic.in at the end";
                }

                if (list.contains("-") || list.contains(".")) {
                } else {
                    msg = "List Name should contain (hyphen(-) or dot[.]";
                }
            }
        } else {
            msg = "Please append @lsmgr.nic.in at the end";
        }
        return msg;
    }
	
    public boolean dnstxtValidation(String add1) {
        boolean flag = false;
        if (add1.isEmpty()) {
            flag = true;

        } else if (!add1.matches("^[^<>&%]{2,300}+$")) {
            flag = true;
        }
        return flag;
    }
    
    public boolean checkradioValidation(String list_mod) {
        boolean flag = false;
        String msg = "";
        if ((!list_mod.equals("")) && (list_mod.equalsIgnoreCase("yes") || list_mod.equalsIgnoreCase("no"))) {
            flag = false;
        } else {
            flag = true;

        }
        return flag;
    }

    public HashMap MailAcceptance(String mailAcceptance) {
        String valid = "false";
        String errorMsg = "";
        values.clear();
        if (mailAcceptance == null || mailAcceptance.equals("")) {
            errorMsg = "MailAcceptance can not be blank.";
        } else {
            if (!mailAcceptance.matches("^[a-z|A-Z|]+[a-z|A-Z|.|\\s]*")) {
                errorMsg = "Enter MailAcceptance in correct format.";
            } else {
                valid = "true";
            }
        }
        values.put("valid", valid);
        values.put("errorMsg", errorMsg);
        return values;
    }
    public HashMap<String, String> Fname(String fname) {
		String valid = "false";
		String errorMsg = "";
		values.clear();
		if (fname == null || fname.equals("")) {
			errorMsg = "First name can not be blank.";
		} else {
			if (!fname.matches("^[a-z|A-Z|]+[a-z|A-Z|.|\\s]*")) {
				errorMsg = "Enter First name in correct format.";
			} else {
				valid = "true";
			}
		}
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}
	
    public HashMap<String, String> onlyMail(String mail) {
		System.out.println(" inside onlyuid function mail is " + mail);
		String valid = "false";
		String errorMsg = "";
		boolean rightm = true;
		if (mail == null || mail.equals("")) {
			rightm = false;
			errorMsg = "Mail can not be blank.";
		} else if (!mail.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) 
																							
		{
			rightm = false;
			errorMsg = "Mail is Invalid";
		} else if (Character.isDigit(mail.charAt(0))) {
			rightm = false;
			errorMsg = "Mail cannot start with a numeric value.";
		} else if (mail.startsWith("-") || mail.startsWith(".")) {
			rightm = false;
			errorMsg = "Mail cannot starts with dot[.] and hyphen[-].";
		} else if (mail.contains("_")) {
			rightm = false;
			errorMsg = "Mail cannot contain underscore[_].";
		} else {
			for (int i1 = 0; i1 < mail.length(); i1++) {
				if (Character.isWhitespace(mail.charAt(i1))) {
					rightm = false;
					errorMsg = "Mail cannot contain whitespaces.";
				}
			}
		}
		if (rightm) {
			valid = "true";
		} else {
			valid = "false";
		}
		System.out.println(
				" inside onlymail function at the end valid value is " + valid + " error msg value is " + errorMsg);
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}
    
}
