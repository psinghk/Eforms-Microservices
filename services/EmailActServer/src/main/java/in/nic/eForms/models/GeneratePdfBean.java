package in.nic.eForms.models;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class GeneratePdfBean {

	private String registrationNo;
	private String preferredEmail1;
	private String dor;
	private String finalId;
	private String empCode;
	private String name;
	private String email;
	private String mobile;
	private String designation;
	private String address;
	private String city;
	private String postingState;
	private String pin;
	private String officePhone;
	private String residencePhone;
	private String userIp;
	private String supportActionTaken;
	private LocalDateTime datetime;
	private String hodName;
	private String hodEmail;
	private String hodMobile;
	private String hodTelephone;
	private String hodDesignation;
	private String employment;
	private String ministry;
	private String department;
	private String otherDept;
	private String state;
	private String organization;
	private String pdfPath;
	private String signCert;	
	private String renameSignCert;
	private String caRenameSignCert;
	private String caSignCert;
	private String emailSent;
	private String smsSent;	
	private String emailSentTo;
	private String smsSentTo;
	private String underSecEmail;	
	private String underSecName;
	private String underSecMobile;
	private String underSecDesig;
	private String underSecTelephone;
	private String empType;   
	private String orderChk;
	private String workrOrder;
	private String selectWeek;
	
	
}
