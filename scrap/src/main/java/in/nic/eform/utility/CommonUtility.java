package in.nic.eform.utility;

import org.springframework.stereotype.Component;

@Component
public class CommonUtility {
	
	public String findRole(String forward) {
		if (forward.equals("")) {
			return "yourself";
		} else if (forward.equals("a")) {
			return "Applicant";
		} else if (forward.matches("c")) {
			return "Coordinator";
		} else if (forward.matches("ca")) {
			return "Reporting/Forwarding/Nodal Officer";
		} else if (forward.equals("d")) {
			return "DA-Admin";
		} else if (forward.equals("m")) {
			return "Admin";
		} else if (forward.equals("s")) {
			return "Support";
		} else if (forward.equals("us")) {
			return "Under Secretary";
		} else {
			return "";
		}
	}
	
	public String fetchRole(String role) {
		 String qr_forwarded_by = "";
	    switch (role) {
        case Constants.ROLE_CA:
            qr_forwarded_by = "ca";
            break;
        case Constants.ROLE_CO:
            qr_forwarded_by = "c";
            break;
        case Constants.ROLE_SUP:
            qr_forwarded_by = "s";
            break;
        case Constants.ROLE_MAILADMIN:
            qr_forwarded_by = "m";
            break;
        case Constants.ROLE_USER:
            qr_forwarded_by = "u";
            break;
    }
		return qr_forwarded_by;
	}
	
	
	
}
