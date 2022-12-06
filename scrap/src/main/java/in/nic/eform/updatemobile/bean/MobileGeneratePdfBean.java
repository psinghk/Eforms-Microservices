package in.nic.eform.updatemobile.bean;

import lombok.Data;

@Data
public class MobileGeneratePdfBean {
	private String registrationno;
	private String applicant_name;
	private String applicant_email;
	private String description;
	private String applicant_mobile;
	private String hod_name;
	private String hod_email;
	private String hod_mobile;
	private String new_mobile;
	private String country_code;
	private String min;
	
}
