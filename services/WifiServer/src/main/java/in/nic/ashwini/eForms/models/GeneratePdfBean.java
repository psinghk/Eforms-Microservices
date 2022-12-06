package in.nic.ashwini.eForms.models;

import lombok.Data;

@Data
public class GeneratePdfBean {
	private String registrationNo;
	private String applicantName;
	private String applicantEmail;
	private String applicantMobile;
	private String hodName;
	private String hodEmail;
	private String hodMobile;
	private String wifiRequest;
	private String ministry;
	
}
