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
@Table(name = "vpn_registration")
@Access(value = AccessType.FIELD)
@Data
public class VpnBase{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="registration_no")
	private String registrationNo; 
	
	@Column(name="user_type")
	private String userType;
	
	@Column(name="ip_type")
	private String ipType;
	
	@Column(name="ip1")
	private String ip1;
	
	@Column(name="ip2")
	private String ip2;
	
	@Column(name="server_location")
	private String serverLocation;
	
	@Column(name="server_loc_other")
	private String serverLocOther;
	
	@Column(name="app_url")
	private String appUrl;
	
	@Column(name="dest_port")
	private String destPort;
	
	@Column(name="pname")
	private String pname;
	
	@Column(name="pdesignation")
	private String pdesignation;
	
	@Column(name="pemail")
	private String pemail;
	
	@Column(name="pmobile")
	private String pmobile;
	
	@Column(name="paddress")
	private String paddress;
	
	@Column(name="uploaded_filename")
	private String uploadedRilename;
	
	@Column(name="renamed_filepath")
	private String renamedFilepath;
	
	@Column(name="emp_code")
	private String empCode;
	
	@Column(name="auth_off_name")
	private String name;
	
	@Column(name="designation")
	private String designation;
	
	@Column(name="address")
	private String address;
	
	@Column(name="city")
	private String city;
	
	@Column(name="add_state")
	private String postingState;
	
	@Column(name="pin")
	private String pin;
	
	@Column(name="ophone")
	private String ophone;
	
	@Column(name="rphone")
	private String rphone;
	
	@Column(name="mobile")
	private String mobile;
	
	@Column(name="auth_email")
	private String email;
	
	@Column(name="userip")
	private String userIp;
	
	@Column(name = "datetime")
	private LocalDateTime datetime;
	
	@Column(name="support_action_taken")
	private String supportActionTaken;
	
	@Column(name="hod_name")
	private String hodName;
	
	@Column(name="hod_email")
	private String hodEmail;
	
	@Column(name="hod_mobile")
	private String hodMobile;
	
	@Column(name="hod_telephone")
	private String hodTelephone;
	
	@Column(name="ca_desig")
	private String hodDesignation; 
	
	@Column(name="employment")
	private String employment;
	
	@Column(name="ministry")
	private String ministry;
	
	@Column(name="department")
	private String department;
	
	@Column(name="other_dept")
	private String otherDept;
	
	@Column(name="state")
	private String state;
	
	@Column(name="organization")
	private String organization;
	
	@Column(name="pdf_path")
	private String pdfPath;
	
	@Column(name="sign_cert")
	private String signCert;
	
	@Column(name="rename_sign_cert")
	private String renameSignCert;
	
	@Column(name="ca_sign_cert")
	private String caSignCert;
	
	@Column(name="ca_rename_sign_cert")
	private String caRenameSignCert;
	
	@Column(name="renew_flag")
	private String renewFlag;
	
	@Column(name="vpn_reg_no")
	private String vpnRegNo;
	
	@Column(name="remarks")
	private String remarks;
	
	@Column(name="coordinator_email")
	private String coordinatorEmail;
}
