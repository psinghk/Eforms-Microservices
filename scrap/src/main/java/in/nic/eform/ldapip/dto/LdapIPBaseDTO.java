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
@Table(name = "ip_registration")
@Access(value = AccessType.FIELD)
@Data
public class LdapIPBaseDTO implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private int id;
		@Column(name = "registration_no")
		private String registrationno;
		
		
		@Column(name = "ip_change_request")
		private String ip_change_request;
		
		@Column(name = "ip_action_request")
		private String ip_action_request;
		
		@Column(name = "account_name")
		private String account_name;
		
		@Column(name = "ip1")
		private String ip1;
		
		@Column(name = "ip2")
		private String 	ip2;
		
		@Column(name = "ip3")
		private String 	ip3;
		
		@Column(name = "ip4")
		private String 	ip4;
		
		@Column(name = "app_name")
		private String app_name;
		
		@Column(name = "app_url")
		private String app_url;
		
		@Column(name = "domain")
		private String domain;
		
		@Column(name = "base_ip")
		private String base_ip;
		
		@Column(name = "service_ip")
		private String service_ip;
		
		@Column(name = "server_loc")
		private String server_loc;
		
		@Column(name = "server_loc_other")
		private String server_loc_txt;
		
		@Column(name = "https")
		private String 	https;
		
		@Column(name = "audit")
		private String audit;
		
		@Column(name = "ldap_id1")
		private String ldap_id1;
		
		@Column(name = "ldap_id2")
		private String ldap_id2;
		
		@Column(name = "uploaded_filename")
		private String uploaded_filename;
		
		@Column(name = "renamed_filepath")
		private String renamed_filepath;
		
		@Column(name = "telnet_response")
		private String 	telnet_response;
		
		@Column(name = "req_id")
		private String req_id;
		
		@Column(name = "emp_code")
		private String applicant_code;
		
		@Column(name = "auth_off_name")
		private String applicant_name;
		
		@Column(name = "designation")
		private String applicant_designation;
		
		@Column(name = "address")
		private String applicant_ofcAddess;
		
		@Column(name = "city")
		private String applicant_posting_city;
		
		@Column(name = "add_state")
		private String 	applicant_posting_state;
		
		@Column(name = "pin")
		private String applicant_pincode;
		
		@Column(name = "ophone")
		private String applicant_ophone;
		
		@Column(name = "rphone")
		private String applicant_rphone;
		
		@Column(name = "mobile")
		private String applicant_mobile;
		
		@Column(name = "auth_email")
		private String applicant_email;
		
		@Column(name = "userip")
		private String userip;
		
		@Column(name = "datetime")
		private String datetime;
		
		@Column(name = "support_action_taken")
		private String support_action_taken;
		
		@Column(name = "hod_name")
		private String 	hod_name;
		
		@Column(name = "hod_email")
		private String hod_email;
		
		@Column(name = "hod_mobile")
		private String 	hod_mobile;
		
		@Column(name = "hod_telephone")
		private String 	hod_telephone;
		
		@Column(name = "ca_desig")
		private String 	hod_designation;
		
		@Column(name = "employment")
		private String 	applicant_employment;
		
		@Column(name = "ministry")
		private String  applicant_ministry;
		
		@Column(name = "department")
		private String 	applicant_department;
		
		@Column(name = "other_dept")
		private String 	applicant_other_department;
		
//		@Column(name = "state")
//		private String 	applicant_posting_state;
		
		@Column(name = "organization")
		private String 	organization;
		
		@Column(name = "pdf_path")
		private String 	pdf_path;
		
		@Column(name = "sign_cert")
		private String 	sign_cert;
		
		@Column(name = "rename_sign_cert")
		private String 	rename_sign_cert;
		
		@Column(name = "ca_sign_cert")
		private String 	ca_sign_cert;
		
		@Column(name = "ca_rename_sign_cert")
		private String 	ca_rename_sign_cert;
		
		
		@Column(name = "app_ip")
		private String 	app_ip;
		
		
		@Column(name = "dept")
		private String 	dept;


		
		@Column(name = "ldap_auth_allocate")
		private String 	ldap_auth_allocate;
		
		@Column(name = "ldap_url")
		private String 	ldap_url;
		
		
}
