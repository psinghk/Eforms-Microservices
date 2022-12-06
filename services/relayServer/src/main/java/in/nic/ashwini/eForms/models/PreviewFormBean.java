package in.nic.ashwini.eForms.models;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data

//@RequestForValid

@Validated

public class PreviewFormBean {

	private String requestFor;

	private String appIp;

	private String oldAppIp;

//List<SmtpIpDetails> smtpIpDetails;

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

	private MultipartFile hardwareCertFile;

	private MultipartFile certFile;

	private String securityExpDate;

	private String isHostedNic;

	private String organization;
	// @NotEmpty
	private String employment;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;
	private String remarks;
	// @NotNull
	private Boolean tnc;
}
