package in.nic.ashwini.eForms.models;

import java.time.LocalDateTime;

import javax.persistence.Column;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
public class ImappopUpdateBean {
	private String registrationNo;
	private String requestFor;
	private String empCode;
	private String name;
	private String email;
	private String mobile;
	private String designation;
	private String address;
	private String city;
	private String userIp;
	private String pin;
	private String officePhone;
	private String residencePhone;
	
	private String organization;
	private String postingState;
	private String 	employment;
	private String 	department;
	private String 	otherDept;
	private String state;
	private String ministry;
	
	private String hodName;
	private String hodEmail;
	private String hodMobile;
	private String 	hodTelephone;
	private String 	hodDesignation;
	
	private String renameSignCert;
	private String caRenameSignCert;
	private String 	caSignCert;
	
	
	private String pdfPath;
	private String description;
	private String 	signCert;
	
	private String supportActionTaken;
	private LocalDateTime datetime;
	private LocalDateTime lastUpdationDateTime;
	private String tnc;

}
