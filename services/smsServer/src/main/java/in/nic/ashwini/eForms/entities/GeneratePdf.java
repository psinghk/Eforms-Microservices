package in.nic.ashwini.eForms.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "sms_registration")
@Data
public class GeneratePdf {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
	 */
	
	@Column(name = "sms_service")
	private String smsService;
	@Column(name = "pull_url")
	private String pullUrl;
	@Column(name="pull_keyword")
	private String pullKeyword;
	@Column(name = "short_flag")
	private String shortFlag;
	@Column(name = "short_note")
	private String shortNote;
	@Column(name = "app_name")
	private String appName;
	@Column(name = "app_url")
	private String appUrl;
	@Column(name = "sms_usage")
	private String smsUsage;
	@Column(name = "server_loc")
	private String serverLoc;
	@Column(name = "server_loc_other")
	private String ServerLocOther;
	@Column(name = "base_ip")
	private String baseIp;
	@Column(name = "service_ip")
	private String serviceIp;
	@Column(name = "tech_name")
	private String techName;
	@Column(name = "tech_desig")
	private String techDesig;
	@Column(name="tech_emp_code")
	private String techEmpCode;
	@Column(name = "tech_address")
	private String 	techAddress;
	@Column(name = "tech_city")
	private String 	techCity;
	@Column(name = "tech_state")
	private String 	techState;
	@Column(name = "tech_pin")
	private String techPin;
	@Column(name = "tech_ophone")
	private String techOphone;	
	@Column(name = "tech_rphone")
	private String techRphone;
	@Column(name = "tech_mobile")
	private String techMobile;
	@Column(name = "tech_email")
	private String techEmail;
	@Column(name = "bowner_name")
	private String bownerName;
	@Column(name = "bowner_desig")
	private String 	bownerDesign;	
	@Column(name = "bowner_emp_code")
	private String bownerEmpCode;
	@Column(name = "bowner_address")
	private String bownerAddress;
	@Column(name = "bowner_city")
	private String 	bownerCity;
	@Column(name = "bowner_state")
	private String bownerState;
	@Column(name = "bowner_pin")
	private String bownerPin;
	@Column(name = "bowner_ophone")
	private String 	bownerPhone;	
	@Column(name = "bowner_rphone")
	private String bownerRphone;
	@Column(name = "bowner_mobile")
	private String bownerMobile;
	@Column(name = "bowner_email")
	private String bownerEmail;
	@Column(name = "audit")     
	private String audit;
	@Column(name = "audit_date")
	private String auditDate;
	@Column(name = "staging_ip")
	private String 	stagingIp;
	@Column(name = "flag_sender")
	private String 	flagSender;
	@Column(name = "sender_id")
	private String senderId;
	@Column(name = "domestic_traffic")
	private String domesticTraffic;
	@Column(name = "inter_traffic")
	private String interTraffic;
	@Column(name = "emp_code")
	private String 	empCode;
	@Column(name = "description")
	private String 	description;
	@Column(name = "auth_off_name")
	private String name;
	@Column(name = "designation")
	private String designation;
	@Column(name = "address")
	private String address;
	@Column(name = "city")
	private String 	city;
	@Column(name = "add_state")
	private String 	addState;
	@Column(name = "pin")
	private String pin;
	@Column(name = "ophone")     
	private String ophone;
	@Column(name = "rphone")
	private String rphone;
	@Column(name = "mobile")
	private String 	mobile;
	@Column(name = "auth_email")
	private String 	authEmail;
	@Column(name = "hod_name")
	private String hodName;
	@Column(name = "hod_email")
	private String hodEmail;
	@Column(name = "hod_mobile")
	private String 	hodMobile;
	@Column(name = "hod_telephone")
	private String 	hodTelephone;
	@Column(name = "ca_desig")
	private String caDesig;
	@Column(name = "employment")
	private String employment;
	@Column(name = "ministry")
	private String ministry;
	@Column(name = "department")
	private String 	department;
	@Column(name = "other_dept")
	private String 	otherDept;
	@Column(name = "state")
	private String state;
	@Column(name = "organization")     
	private String organization;
	@Column(name = "userip")
	private String userIp;
	
	@Column(name = "registration_no")
	private String 	registrationNo;
	@Column(name = "support_action_taken")
	private String supportActionTaken;
	@Column(name = "final_id")
	private String finalId;
	@Column(name = "telnet_response")
	private String 	telnetResponse;
	@Column(name = "req_id")
	private String 	regId;
	@Column(name = "pdf_path")
	private String pdfPath;
	@Column(name = "sign_cert")    
	private String signCert;
	@Column(name = "rename_sign_cert")
	private String renameSignCert;
	@Column(name = "ca_sign_cert")
	private String 	caSignCert;
	@Column(name = "ca_rename_sign_cert")
	private String 	caRenameSignCert;
	
	//@CreationTimestamp
	@Column(name = "datetime")
	private LocalDateTime datetime;
	//@Column(name = "last_updated")
	//private LocalDateTime lastUpdationDateTime;
	
}
