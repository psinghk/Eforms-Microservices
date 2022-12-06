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

import lombok.Data;
@Entity
@Table(name = "sms_registration")
@Access(value = AccessType.FIELD)
@Data
public class SmsBase{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "sms_service")
	private String sms_service;
	@Column(name = "pull_url")
	private String pull_url;
	@Column(name="pull_keyword")
	private String pull_keyword;
	@Column(name = "short_flag")
	private String s_code;
	@Column(name = "short_note")
	private String short_code;
	@Column(name = "app_name")
	private String app_name;
	@Column(name = "app_url")
	private String app_url;
	@Column(name = "sms_usage")
	private String sms_usage;
	@Column(name = "server_loc")
	private String server_loc;
	@Column(name = "server_loc_other")
	private String server_loc_txt;
	@Column(name = "base_ip")
	private String base_ip;
	@Column(name = "service_ip")
	private String service_ip;
	@Column(name = "tech_name")
	private String t_off_name;
	@Column(name = "tech_desig")
	private String tdesignation;
	@Column(name="tech_emp_code")
	private String temp_code;
	@Column(name = "tech_address")
	private String 	taddrs;
	@Column(name = "tech_city")
	private String 	tcity;
	@Column(name = "tech_state")
	private String 	tstate;
	@Column(name = "tech_pin")
	private String tpin;
	@Column(name = "tech_ophone")
	private String ttel_ofc;	
	@Column(name = "tech_rphone")
	private String ttel_res;
	@Column(name = "tech_mobile")
	private String tmobile;
	@Column(name = "tech_email")
	private String tauth_email;
	
	@Column(name = "bowner_name")
	private String bauth_off_name;
	@Column(name = "bowner_desig")
	private String 	bdesignation;	
	@Column(name = "bowner_emp_code")
	private String bemp_code;
	@Column(name = "bowner_address")
	private String baddrs;
	@Column(name = "bowner_city")
	private String 	bcity;
	@Column(name = "bowner_state")
	private String bstate;
	@Column(name = "bowner_pin")
	private String bpin;
	@Column(name = "bowner_ophone")
	private String 	btel_ofc;	
	@Column(name = "bowner_rphone")
	private String btel_res;
	@Column(name = "bowner_mobile")
	private String bmobile;
	@Column(name = "bowner_email")
	private String bauth_email;
	
	@Column(name = "audit")     
	private String audit;
	@Column(name = "audit_date")
	private String datepicker1;
	@Column(name = "staging_ip")
	private String 	staging_ip;
	@Column(name = "flag_sender")
	private String 	sender;
	@Column(name = "sender_id")
	private String sender_id;
	@Column(name = "domestic_traffic")
	private String domestic_traf;
	@Column(name = "inter_traffic")
	private String inter_traf;
	
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
	private String 	postingState;
	@Column(name = "pin")
	private String pin;
	@Column(name = "ophone")     
	private String ophone;
	@Column(name = "rphone")
	private String rphone;
	@Column(name = "mobile")
	private String 	mobile;
	@Column(name = "auth_email")
	private String 	email;
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
	
}
