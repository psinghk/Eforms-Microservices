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

import com.sun.istack.Nullable;

import lombok.Data;
@Entity
@Table(name = "dns_registration")
@Access(value = AccessType.FIELD)
@Data
public class DnsBase{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	@Column(name = "dns_type")
	private String dnsType;
	@Column(name = "record_aaaa")
	private String aRecord;
	@Column(name = "record_mx")
	private String mxRecord;
	@Column(name = "record_mx1")
	private String mx1Record;
	@Column(name = "record_ptr")
	private String ptrRecord;
	@Column(name = "record_ptr1")
	private String ptr1Record;
	@Column(name = "record_srv")
	private String srvRecord;
	@Column(name = "record_spf")
	private String spfRecord;
	@Column(name = "record_txt")
	private String txtRecord;
	@Column(name = "record_dmarc")
	private String dmarcRecord;
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
	@Column(name = "sign_cert")
	private String 	signCert;
	
	@Column(name = "pdf_path")
	private String pdfPath;
	@Column(name = "form_type")
	private String formType;
	@Column(name = "req_id")
	private String reqId;
	@Column(name = "req_for")
	private String reqFor;
	@Column(name = "old_url")
	private String oldUrl;
	@Column(name = "old_ip")
	private String oldIp;
	
	@Column(name = "email_sent")
	@Nullable
	private String emailSent;
	@Column(name = "email_sent_to")
	private String emailSentTo;
	@Column(name = "sms_sent")
	private String smsSent;
	@Column(name = "sms_sent_to")
	private String smsSentTo;
	
	@Column(name = "support_action_taken")
	private String supportActionTaken;
	@Column(name = "telnet_response")
	private String telnetResponse;
	
	@Column(name = "uploaded_filename")
	private String uploadedFilename;
	@Column(name = "renamed_filepath")
	private String renamedFilepath;
	
	@Column(name = "server_location")
	private String serverLocation;
	@Column(name = "service_url")
	private String serviceUrl;
	@Column(name = "migration_date")
	private String migrationDate;
	@Column(name = "req_other_record")
	private String reqOtherRecord;
	
	@CreationTimestamp
	@Column(name = "datetime")
	private LocalDateTime datetime;
	@Column(name = "last_updated")
	private LocalDateTime lastUpdationDateTime;
}
