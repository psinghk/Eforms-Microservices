package in.nic.ashwini.eForms.entities;

import java.time.LocalDateTime;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Table(name = "ldap_registration")
@Access(value = AccessType.FIELD)
@Data
public class LdapBase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	
	@Column(name = "app_name")
	private String appName;
	
	@Column(name = "app_url")
	private String appUrl;
	
	@Column(name = "domain")
	private String domain;
	
	@Column(name = "base_ip")
	private String baseIp;
	
	@Column(name = "service_ip")
	private String serviceIp;
	
	@Column(name = "server_loc")
	private String serverLoc;
	
	@Column(name = "server_loc_other")
	private String serverLocTxt;
	
	@Column(name = "https")
	private String 	https;
	
	@Column(name = "audit")
	private String audit;
	
	@Column(name = "ldap_id1")
	private String ldap_id1;
	
	@Column(name = "ldap_id2")
	private String ldap_id2;
	
	@Column(name = "uploaded_filename")
	private String uploadedFilename;
	
	@Column(name = "renamed_filepath")
	private String renamedFilepath;
	
	@Column(name = "telnet_response")
	private String 	telnetResponse;
	
	@Column(name = "req_id")
	private String reqId;
	
	@Column(name="emp_code")
	private String empCode;
	@Column(name = "auth_off_name")
	private String name;
	@Column(name = "auth_email")
	private String email;
	@Column(name = "mobile")
	private String mobile;
	@Column(name = "designation")
	private String designation;
	@Column(name = "address")
	private String address;
	@Column(name = "city")
	private String city;
	@Column(name = "userip")
	private String userIp;
	@Column(name = "pin")
	private String pin;
	@Column(name = "ophone")
	private String officePhone;
	@Column(name = "rphone")
	private String residencePhone;
	
	@Column(name = "organization")
	private String organization;
	@Column(name="add_state")
	private String postingState;
	@Column(name = "employment")
	private String 	employment;
	@Column(name = "department")
	private String 	department;
	@Column(name = "other_dept")
	private String 	otherDept;
	@Column(name = "state")
	private String state;
	@Column(name = "ministry")
	private String ministry;
	
	@Column(name = "hod_name")
	private String hodName;
	@Column(name = "hod_email")
	private String hodEmail;
	@Column(name = "hod_mobile")
	private String hodMobile;
	@Column(name = "hod_telephone")
	private String 	hodTelephone;
	@Column(name = "ca_desig")
	private String 	hodDesignation;
	
	@Column(name = "rename_sign_cert")
	private String renameSignCert;
	@Column(name = "ca_rename_sign_cert")
	private String caRenameSignCert;
	@Column(name = "ca_sign_cert")
	private String 	caSignCert;
	
	
	@Column(name = "pdf_path")
	private String pdfPath;
//	@Column(name = "description")
//	private String description;
	@Column(name = "sign_cert")
	private String 	signCert;
	
	@Column(name = "support_action_taken")
	private String supportActionTaken;
	@CreationTimestamp
	@Column(name = "datetime")
	private LocalDateTime datetime;
//	@Column(name = "last_updated")
//	private LocalDateTime lastUpdationDateTime;
	
	
	
}
