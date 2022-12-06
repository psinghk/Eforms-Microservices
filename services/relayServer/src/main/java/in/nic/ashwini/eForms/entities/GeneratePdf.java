package in.nic.ashwini.eForms.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Entity
@Table(name = "relay_registration")
@Data
public class GeneratePdf {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	@Column(name = "registration_no")
	private String registrationNo;
	@Column(name = "auth_off_name")
	private String applicantName;
	@Column(name = "auth_email")
	private String applicantEmail;
//	@Column(name = "description")
//	private String description;
	@Column(name = "ministry")
	private String ministry;
	@Column(name = "mobile")
	private String applicantMobile;
	@Column(name = "hod_name")
	private String hodName;
	@Column(name = "hod_email")
	private String hodEmail;
	@Column(name = "hod_mobile")
	private String hodMobile;
	
	
	

	@Column(name = "req_type")
	private String requestFor;

	@Column(name = "app_ip")
	private String appIp;

	@Column(name = "relay_old_ip")
	private String oldAppIp;

	@Column(name = "security_audit")
	private String securityAudit;

	@Column(name = "app_name")
	private String appName;

	@Column(name = "relay_app_url")
	private String appUrl;

	@Column(name = "division_name")
	private String divisionName;

	@Column(name = "os")
	private String os;

	@Column(name = "server_loc")
	private String serverLoc;

	@Column(name = "server_loc_other")
	private String otherServerLoc;

	@Column(name = "port")
	private String port;

	@Column(name = "relay_sender_id")
	private String senderId;

	@Column(name = "domain_mx")
	private String mxDomain;

	@Column(name = "relay_mailsent")
	private String mailNumber;

	@Column(name = "mail_type")
	private String mailType;

	@Column(name = "point_contact")
	private String pointContact;

	@Column(name = "point_name")
	private String pointName;

	@Column(name = "point_email")
	private String pointEmail;

	@Column(name = "point_mobile_number")
	private String pointMobileNumber;

	@Column(name = "landline_number")
	private String landlineNumber;

	@Column(name = "relay_auth_id")
	private String relayAuthId;

	@Column(name = "staging_ip")
	private String stagingIp;

	@Column(name = "hardware_filename")
	private String hardwareCertFile;

	@Column(name = "uploaded_filename")
	private String certFile;

	@Column(name = "security_exp_date")
	private String securityExpDate;

	@Column(name = "is_hosted_nic")
	private String isHostedNic;
	
	
}
