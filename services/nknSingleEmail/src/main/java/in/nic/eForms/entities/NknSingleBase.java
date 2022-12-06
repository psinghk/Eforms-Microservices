package in.nic.eForms.entities;

import java.time.LocalDateTime;
import java.time.LocalDate;
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
@Table(name = "nkn_registration")
@Access(value = AccessType.FIELD)
@Data
public class NknSingleBase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	
	@Column(name = "request_type")
	private String request_type;
	
	@Column(name = "inst_name")
	private String inst_name;
	
	@Column(name = "inst_id")
	private String inst_id;
	
	@Column(name = "nkn_project")
	private String nkn_project;
	
	@Column(name = "dob")
	private LocalDate single_dob;
	
	@Column(name = "dor")
	private LocalDate single_dor;
	
	@Column(name = "preferred_email1")
	private String preferred_email1;
	
	@Column(name = "preferred_email2")
	private String preferred_email2;
	
	@Column(name = "preferred_uid1")
	private String preferred_uid1;
	
	@Column(name = "preferred_uid2")
	private String preferred_uid2;
	
	@Column(name = "uploaded_filename")
	private String 	uploaded_filename;
	
	@Column(name = "renamed_filepath")
	private String renamed_filepath;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "final_id")
	private String final_id;
	
	
	
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
	
	@Column(name = "under_sec_name")
	private String 	under_sec_name;
	@Column(name = "under_sec_email")
	private String under_sec_email;
	@Column(name = "under_sec_desig")
	private String under_sec_desig;
	@Column(name = "under_sec_mobile")
	private String 	under_sec_mobile;
	@Column(name = "under_sec_telephone")
	private String 	under_sec_telephone;
	
	
	@Column(name = "rename_sign_cert")
	private String renameSignCert;
	@Column(name = "ca_rename_sign_cert")
	private String caRenameSignCert;
	@Column(name = "ca_sign_cert")
	private String 	caSignCert;
	
	
	@Column(name = "pdf_path")
	private String pdfPath;
//	@Column(name = "description")
//	private String description;
	@Column(name = "sign_cert")
	private String 	signCert;
	
	@Column(name = "support_action_taken")
	private String supportActionTaken;
	@CreationTimestamp
	@Column(name = "datetime")
	private LocalDateTime datetime;
//	@Column(name = "last_updated")
//	private LocalDateTime lastUpdationDateTime;
	
	
	
}
