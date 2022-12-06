package in.nic.ashwini.eForms.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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
	@Column(name = "description")
	private String description;
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
	
	
	@Column(name="app_name")
	private String appName;
	
	@Column(name = "os")
	private String os;
	
	@Column(name="app_ip")
	private String appIp;
	
	@Column(name="relay_app_url")
	private String relayAppUrl;
	
	@Column(name="relay_sender_id")
	private String relaySenderId;
	
	@Column(name="domain_mx")
	private String domainMx;
	
	@Column(name="port")
	private String port;
	
	@Column(name="spf")
	private String spf;
	
	@Column(name="dkim")
	private String dkim;
	
	@Column(name="dmarc")
	private String dmarc;
	
	@Column(name="relay_auth_id")
	private String relayAuthId;
	
	@Column(name="old_app_id")
	private String oldAppId;
	
	@Column(name="new_app_id")
	private String newAppId;
	
}
