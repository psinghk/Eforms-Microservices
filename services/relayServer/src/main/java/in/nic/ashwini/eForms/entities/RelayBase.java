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
@Table(name = "relay_registration")
@Access(value = AccessType.FIELD)
@Data
public class RelayBase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "registration_no")
	private String registrationNo;

	@Column(name = "division_name")
	private String divisionName;

	@Column(name = "app_name")
	private String appName;

	@Column(name = "os")
	private String os;

	@Column(name = "app_ip")
	private String appIp;

	@Column(name = "staging_ip")
	private String stagingIp;

	@Column(name = "server_loc")
	private String serverLoc;

	@Column(name = "server_loc_other")
	private String otherServerLoc;;

	@Column(name = "uploaded_filename")
	private String certFile;

	@Column(name = "renamed_filepath")
	private String renamedFilepath;

	@Column(name = "telnet_response")
	private String telnetResponse;

	@Column(name = "req_id")
	private String reqId;

	@Column(name = "emp_code")
	private String empCode;

	@Column(name = "auth_off_name")
	private String name;

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
	private String email;

	@Column(name = "userip")
	private String userip;

	@Column(name = "datetime")
	private LocalDateTime datetime;

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

	@Column(name = "ca_sign_cert")
	private String caSignCert;

	@Column(name = "ca_rename_sign_cert")
	private String caRenameSignCert;

	@Column(name = "relay_app_url")
	private String appUrl;

	@Column(name = "domain_mx")
	private String mxDomain;

	@Column(name = "port")
	private String port;

	@Column(name = "spf")
	private String spf;

	@Column(name = "dkim")
	private String dkim;

	@Column(name = "dmarc")
	private String dmarc;

	@Column(name = "relay_auth_id")
	private String relayAuthId;

	@Column(name = "relay_old_ip")
	private String oldAppIp;

	@Column(name = "req_type")
	private String requestFor;

	@Column(name = "relay_sender_id")
	private String senderId;

	@Column(name = "relay_mailsent")
	private String mailNumber;

	@Column(name = "point_mobile_number")
	private String pointMobileNumber;

	@Column(name = "point_email")
	private String pointEmail;

	@Column(name = "point_name")
	private String pointName;

	@Column(name = "landline_number")
	private String landlineNumber;

	@Column(name = "security_audit")
	private String securityAudit;

	@Column(name = "security_exp_date")
	private String securityExpDate;

	@Column(name = "other_mail_type")
	private String otherMailType;

	@Column(name = "point_contact")
	private String pointContact;

	@Column(name = "is_hosted_nic")
	private String isHostedNic;

	@Column(name = "mail_type")
	private String mailType;

	@Column(name = "hardware_filename")
	private String hardwareCertFile;

	@Column(name = "renamed_hardware_filepath")
	private String renamedHardwareFilepath;

}
