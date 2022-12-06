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
@Table(name = "mobile_registration")
@Access(value = AccessType.FIELD)
@Data
public class MobileBase{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="registration_no")
	private String registrationNo;
	
	@Column(name="country_code")
	private String country_code;
	
	@Column(name="new_mobile")
	private String new_mobile;
	
	@Column(name="remarks")
	private String remarks;

	@Column(name="remarks_flag")
	private String remarksFlag;
	
	
	@Column(name ="other_remarks")
	private String other_remarks;
//	@Column(name="uploaded_filename")
//	private String uploadedFilename;
//	
//	@Column(name="renamed_filepath")
//	private String renamedFilepath;
	
//	@Column(name="telnet_response")
//	private String telnetResponse;
	
//	@Column(name="req_id")
//	private String reqId;
	
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
	private String userip;
	
	@Column(name="datetime")
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
	private String caDesig;
	
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
	
	
	
	
	

	
//	@Column(name = "last_updated")
//	private LocalDateTime lastUpdationDateTime;
}
