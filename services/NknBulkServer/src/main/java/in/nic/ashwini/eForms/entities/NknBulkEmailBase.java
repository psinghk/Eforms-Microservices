package in.nic.ashwini.eForms.entities;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Entity
@Table(name = "nkn_registration")
@Access(value = AccessType.FIELD)
@Data
public class NknBulkEmailBase {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	
//request_type
	@Column(name = "inst_name") 
	private String instName;
	
	@Column(name = "inst_id")
	private String instId;
	@Column(name = "nkn_project")
	private String nknProject;
	
	@Column(name = "dob")
	private String dob;
	@Column(name = "dor")
	private String dor;

	
	@Column(name = "preferred_email1")
	private String preferredEmail1;
	@Column(name = "preferred_email2")
	private String preferredEmail2;
	
	@Column(name = "preferred_uid1")
	private String preferredUid1;
	@Column(name = "preferred_uid2")
	private String preferredUid2;
	
	
	@Column(name="uploaded_filename")
	private String uploadedFilename;
	
	@Column(name="renamed_filepath")
	private String renamedFilepath;

	@Column(name="description")
	private String description;
	
	@Column(name = "final_id")
	private String finalId;
	
	@Column(name = "emp_code")
	private String empCode;
	
	/*
	 * @Column(name = "other_applicant_dept") private String otherApplicantDept;
	 * 
	 * @Column(name = "other_applicant_name") private String otherApplicantName;
	 * 
	 * @Column(name = "other_applicant_email") private String otherApplicantEmail;
	 */

	
	@Column(name = "under_sec_email")
	private String underSecEmail;
	@Column(name = " under_sec_name")
	private String underSecName;
	@Column(name = "under_sec_mobile")
	private String underSecMobile;
	@Column(name = "under_sec_desig")
	private String underSecDesig;
	@Column(name = "under_sec_telephone")
	private String underSecTelephone;
	
	/*
	 * @Column(name = " order_chk") private String orderChk;
	 * 
	 * @Column(name = "work_order") private String workOrder;
	 * 
	 * @Column(name = "select_week") private String selectWeek;
	 */

	
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


	private String organization;
	@Column(name = "add_state")
	private String postingState;
	@Column(name = "employment")
	private String employment;
	
	@Column(name = "department")
	private String department;
	
	@Column(name = "other_dept")
	private String otherDept;
	
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
	private String hodTelephone;
	@Column(name = "ca_desig")
	private String hodDesignation;

	@Column(name = "rename_sign_cert")
	private String renameSignCert;
	@Column(name = "ca_rename_sign_cert")
	private String caRenameSignCert;
	@Column(name = "ca_sign_cert")
	private String caSignCert;

	@Column(name = "pdf_path")
	private String pdfPath;
	
	@Column(name = "sign_cert")
	private String signCert;

	@Column(name = "support_action_taken")
	private String supportActionTaken;
	@CreationTimestamp
	@Column(name = "datetime")
	private LocalDateTime datetime;
//	@Column(name = "last_updated")
//	private LocalDateTime lastUpdationDateTime;
}
