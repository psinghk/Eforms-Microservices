package in.nic.ashwini.eForms.models;

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
	
	private String list_name;
	private String description_list;
	private String list_mod;
	private String allowed_member;
	private String non_nicnet;
	private String list_temp;
	private String memberCount;
	private String validDate;
	
}
