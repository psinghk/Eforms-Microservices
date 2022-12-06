package in.nic.eForms.models;

import lombok.Data;

@Data
public class GeneratePdfBean {
	private String registrationNo;
	private String applicantName;
	private String applicantEmail;
	private String description;
	private String applicantMobile;
	private String hodName;
	private String hodEmail;
	private String hodMobile;
	private String ministry;
	
	private String inst_name;
	private String inst_id;
	private String nkn_project;
	private String single_dob;
	private String single_dor;
	private String preferred_email1;
	private String preferred_email2;
	

	
}
