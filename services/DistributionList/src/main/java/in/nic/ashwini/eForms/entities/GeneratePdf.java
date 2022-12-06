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
@Table(name = "distribution_registration")
@Data
public class GeneratePdf {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	@Column(name = "list_name")
	private String list_name;
	@Column(name = "list_description")
	private String description_list;
	@Column(name = "list_moderated")
	private String list_mod;
	@Column(name = "allowed_member_mail")
	private String allowed_member;
	@Column(name = "other_member_mail")
	private String otherMemberMail;
	@Column(name = "list_temp")
	private String list_temp;
	@Column(name = "valid_date")
	private String validDate;
	@Column(name = "member_count")
	private String memberCount;
	
	@Column(name = "moderator_name")
	private String moderatorName;
	@Column(name = "moderator_email")
	private String moderatorEmail;
	@Column(name = "moderator_mobile")
	private String moderatorMobile;
	@Column(name = "emp_code")
	private String empCode;
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
	private String 	authEmail;
	@Column(name = "userip")
	private String userIp;
	@Column(name = "support_action_taken")
	private String supportActionTaken;
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
	@Column(name = "email_sent")
	private String emailSent;
	@Column(name = "sms_sent")
	private String smsSent;
	@Column(name = "email_sent_to")
	private String emailSentTo;
	@Column(name = "sms_sent_to")
	private String smsSentTo;
	//@CreationTimestamp
	@Column(name = "datetime")
	private LocalDateTime datetime;
	
}
