package in.nic.eForms.entities;

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
@Table(name = "email_deact_registration")
@Access(value = AccessType.FIELD)
@Data
public class EmailDeActBase { 
	   
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	
	@Column(name = "preferred_email1") 	// preferred email address1
	private String preferredEmail;
	@Column(name = "final_id")
	private String finalId;
	@Column(name = "emp_code")
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
	@Column(name = "add_state")
	private String postingState;
	@Column(name = "pin")
	private String pin;
	@Column(name = "ophone")
	private String officePhone;
	@Column(name = "rphone")
	private String residencePhone;
	@Column(name = "userip")
	private String userIp;
	@Column(name = "support_action_taken")
	private String supportActionTaken;
	@CreationTimestamp
	@Column(name = "datetime")
	private LocalDateTime datetime;
	@Column(name = "hod_name")
	private String hodName;
	@Column(name = "hod_email")
	private String hodEmail;
	@Column(name = "hod_mobile")
	private String hodMobile;
	@Column(name = "hod_telephone")
	private String hodTelephone;
	@Column(name = "ca_desig")
	private String hodDesignation;
	@Column(name = "employment")
	private String employment;
	@Column(name = "ministry")
	private String ministry;
	@Column(name = "department")
	private String department;
	@Column(name = "other_dept")
	private String otherDept;
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
	@Column(name = "ca_rename_sign_cert")
	private String caRenameSignCert;
	@Column(name = "ca_sign_cert")
	private String caSignCert;
	@Column(name = "email_sent")
	private String emailSent;
	@Column(name = "sms_sent")
	private String smsSent;
	@Column(name = "email_sent_to")
	private String emailSentTo;
	@Column(name = "sms_sent_to")
	private String smsSentTo;
	@Column(name = "under_sec_email")
	private String underSecEmail;
	@Column(name = "under_sec_name")
	private String underSecName;
	@Column(name = "under_sec_mobile")
	private String underSecMobile;
	@Column(name = "under_sec_desig")
	private String underSecDesig;
	@Column(name = "under_sec_telephone")
	private String underSecTelephone;


}
