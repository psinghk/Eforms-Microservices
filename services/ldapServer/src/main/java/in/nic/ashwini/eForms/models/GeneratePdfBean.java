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
	private String appName;
	private String appUrl;
	private String domain;
	private String baseIp;
	private String serviceIp;
	private String serverLoc;
	//private String serverLocTxt;
	private String https;
	private String audit;
//	private String ldap_id1;
//	private String ldap_id2;
//	private String uploadedFilename;
//	private String renamedFilepath;
//	private String telnetResponse;
//	private String reqId;
	
}
