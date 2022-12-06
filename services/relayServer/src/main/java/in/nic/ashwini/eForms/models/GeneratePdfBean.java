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
	// private String protocol;

	private String ministry;

	private String requestFor;

	private String appIp;

	private String oldAppIp;

	private String securityAudit;

	private String appName;

	private String appUrl;

	private String divisionName;

	private String os;

	private String serverLoc;

	private String otherServerLoc;

	private String port;

	private String senderId;

	private String mxDomain;

	private String mailNumber;

	private String mailType;

	private String pointContact;

	private String pointName;

	private String pointEmail;

	private String pointMobileNumber;

	private String landlineNumber;

	private String relayAuthId;

	private String stagingIp;

	private String hardwareCertFile;

	private String certFile;

	private String securityExpDate;

	private String isHostedNic;
}
