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
	//private String protocol;
	private String ministry;
	private String appName;
	private String os;
	private String appIp;
	private String relayAppUrl;
	private String relaySenderId;
	private String domainMx;
	private String port;
	private String spf;
	private String dkim;
	private String dmarc;
	private String relayAuthId;
	private String oldAppId;
	private String newAppId;
}
