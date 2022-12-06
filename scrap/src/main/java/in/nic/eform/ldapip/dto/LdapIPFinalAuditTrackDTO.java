package in.nic.eform.ldapip.dto;

import java.io.Serializable;
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
@Table(name = "final_audit_track")
@Access(value = AccessType.FIELD)
@Data
public class LdapIPFinalAuditTrackDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int track_id;
	@Column(name = "registration_no")
	private String registrationno;
	@Column(name = "applicant_email")
	private String applicantemail;
	@Column(name = "applicant_mobile")
	private String applicant_mobile;
	@Column(name = "applicant_name")
	private String applicant_name;
	@Column(name = "applicant_ip")
	private String applicant_ip;
	@Column(name = "applicant_datetime")
	private String applicant_datetime;
	@Column(name = "applicant_remarks")
	private String applicant_remarks;
	@Column(name = "to_email")
	private String toemail;
	@Column(name = "to_name")
	private String to_name;
	@Column(name = "to_mobile")
	private String to_mobile;
	@Column(name = "to_datetime")
	private String to_datetime;
	@Column(name = "status")
	private String status;
	@Column(name = "form_name")
	private String form_name;
	
	@Column(name = "ca_email")
	private String caemail;
	@Column(name = "ca_mobile")
	private String ca_mobile;
	@Column(name = "ca_name")
	private String ca_name;
	@Column(name = "ca_ip")
	private String ca_ip;
	@Column(name = "ca_datetime")
	private String ca_datetime;
	@Column(name = "ca_remarks")
	private String ca_remarks;
	
	@Column(name = "us_email")
	private String us_email;
	@Column(name = "us_mobile")
	private String us_mobile;
	@Column(name = "us_name")
	private String us_name;
	@Column(name = "us_ip")
	private String us_ip;
	@Column(name = "us_datetime")
	private String us_datetime;
	@Column(name = "us_remarks")
	private String us_remarks;
	
	@Column(name = "coordinator_email")
	private String coordinatoremail;
	@Column(name = "coordinator_mobile")
	private String coordinator_mobile;
	@Column(name = "coordinator_name")
	private String coordinator_name;
	@Column(name = "coordinator_ip")
	private String coordinator_ip;
	@Column(name = "coordinator_datetime")
	private String coordinator_datetime;
	@Column(name = "coordinator_remarks")
	private String coordinator_remarks;
	
	@Column(name = "support_email")
	private String supportemail;
	@Column(name = "support_mobile")
	private String support_mobile;
	@Column(name = "support_name")
	private String support_name;
	@Column(name = "support_ip")
	private String support_ip;
	@Column(name = "support_datetime")
	private String support_datetime;
	@Column(name = "support_remarks")
	private String support_remarks;
	
	@Column(name = "da_email")
	private String daemail;
	@Column(name = "da_mobile")
	private String da_mobile;
	@Column(name = "da_name")
	private String da_name;
	@Column(name = "da_ip")
	private String da_ip;
	@Column(name = "da_datetime")
	private String da_datetime;
	@Column(name = "da_remarks")
	private String da_remarks;
	
	@Column(name = "admin_email")
	private String adminemail;
	@Column(name = "admin_mobile")
	private String admin_mobile;
	@Column(name = "admin_name")
	private String admin_name;
	@Column(name = "admin_ip")
	private String admin_ip;
	@Column(name = "admin_datetime")
	private String admin_datetime;
	@Column(name = "admin_remarks")
	private String admin_remarks;
	
	@Column(name = "ca_sign_cert")
	private String ca_sign_cert;
	
	@Column(name = "ca_rename_sign_cert")
	private String ca_rename_sign_cert;
	
	@Column(name = "app_user_type")
	private String 	app_user_type;
	@Column(name = "app_ca_type")	
	private String app_ca_type;
	@Column(name = "sign_cert")	
	private String sign_cert;
	@Column(name = "rename_sign_cert")	
	private String rename_sign_cert;
	
	@Column(name = "app_user_path")
	private String app_user_path;
	
	@Column(name = "app_ca_path")
	private String app_ca_path;
	
	@Column(name = "on_hold")
	private String on_hold;
	@Column(name = "hold_remarks")
	private String 	hold_remarks;
}
