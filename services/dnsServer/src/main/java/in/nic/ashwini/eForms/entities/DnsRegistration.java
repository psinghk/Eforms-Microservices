package in.nic.ashwini.eForms.entities;

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
@Table(name = "dns_registration")
@Access(value = AccessType.FIELD)
@Data
public class DnsRegistration {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	@Column(name = "dns_type")
	private String dnsType;
	@Column(name = "record_aaaa")
	private String recordAaaa;
	@Column(name = "record_mx")
	private String recordMx;
	@Column(name = "record_ptr")
	private String recordPtr;
	@Column(name = "record_srv")
	private String recordSrv;
	@Column(name = "record_spf")
	private String recordSpf;
	@Column(name = "record_txt")
	private String recordTxt;
	@Column(name = "record_dmarc")
	private String recordDmarc;
	@Column(name = "emp_code")
	private String empCode;
	@Column(name = "auth_off_name")
	private String authOffName;
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
	private String ophone;
	@Column(name = "rphone")
	private String rphone;
	@Column(name = "mobile")
	private String mobile;
	@Column(name = "auth_email")
	private String authEmail;
	@Column(name = "userip")
	private String userip;
	@Column(name = "datetime")
	private String datetime;
	@Column(name = "support_action_taken")
	private String supportActionTaken;
	@Column(name = "hod_name")
	private String hodName;
	@Column(name = "hod_email")
	private String hodEmail;
	@Column(name = "hod_mobile")
	private String hodMobile;
	@Column(name = "hod_telephone")
	private String hodTelephone;
	@Column(name = "ca_desig")
	private String hodDesig;
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
	@Column(name = "telnet_response")
	private String telnetResponse;
	@Column(name = "req_id")
	private String req_id;
	@Column(name = "pdf_path")
	private String pdfPath;
	@Column(name = "req_for")
	private String reqFor;
	@Column(name = "old_url")
	private String oldUrl;
	@Column(name = "old_ip")
	private String oldIp;
	@Column(name = "sign_cert")
	private String signCert;
	@Column(name = "rename_sign_cert")
	private String renameSignCert;
	@Column(name = "ca_sign_cert")
	private String caSignCert;
	@Column(name = "ca_rename_sign_cert")
	private String caRenameSignCert;
	@Column(name = "email_sent")
	private String emailSent;
	@Column(name = "sms_sent")
	private String smsSent;
	@Column(name = "email_sent_to")
	private String emailSentTo;
	@Column(name = "sms_sent_to")
	private String smmSentTo;
	@Column(name = "form_type")
	private String formType;
	@Column(name = "uploaded_filename")
	private String uploadedFilename;
	@Column(name = "renamed_filepath")
	private String renamedFilepath;
	@Column(name = "server_location")
	private String serverLocation;
	@Column(name = "record_mx1")
	private String recordMx1;
	@Column(name = "record_ptr1")
	private String recordPtr1;
	@Column(name = "service_url")
	private String serviceUrl;
	@Column(name = "migration_date")
	private String migrationDate;
	@Column(name = "req_other_record")
	private String reqOtherRecord;
}
