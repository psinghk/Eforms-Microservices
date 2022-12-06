package in.nic.eform.imappop.dto;

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
@Table(name = "imappop_registration")
@Access(value = AccessType.FIELD)
@Data
public class ImappopBaseDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO) 
	@Column(name = "id")
	private long id;
	@Column(name = "registration_no")
	private String registrationno;
	@Column(name = "auth_off_name")
	private String applicant_name;
	@Column(name = "auth_email")
	private String applicant_email;
	@Column(name = "ministry")
	private String min;
	@Column(name = "mobile")
	private String applicant_mobile;
	@Column(name = "hod_name")
	private String hod_name;
	@Column(name = "hod_email")
	private String hod_email;
	@Column(name = "hod_mobile")
	private String hod_mobile;
	@Column(name = "protocol")
	private String protocol;
	@Column(name = "rename_sign_cert")
	private String rename_sign_cert;
	@Column(name = "ca_rename_sign_cert")
	private String ca_rename_sign_cert;
	@Column(name = "pdf_path")
	private String pdf_path;
	@Column(name = "description")
	private String description;
	@Column(name = "userip")
	private String userip;
	@Column(name = "organization")
	private String org;
	@Column(name="add_state")
	private String state;
	
	@Column(name = "sign_cert")
	private String 	sign_cert;
	
	@Column(name = "ca_sign_cert")
	private String 	ca_sign_cert;
	@Column(name = "designation")
	private String applicant_designation;
	@Column(name = "ca_desig")
	private String 	hod_designation;
	

	@Column(name = "employment")
	private String 	user_employment;
	
	@Column(name = "department")
	private String 	dept;
	
	@Column(name = "other_dept")
	private String 	other_dept;
	
	@Column(name = "state")
	private String add_state;
	
	@Column(name = "address")
	private String applicant_ofcAddess;
	@Column(name = "city")
	private String applicant_posting_city;
//	@Column(name = "add_state")
//	private String applicant_posting_state;
//	
	
	
	@Column(name = "pin")
	private String applicant_pincode;
	
	@Column(name = "ophone")
	private String applicant_ophone;
	
	@Column(name = "rphone")
	private String applicant_rphone;
	
	@Column(name = "support_action_taken")
	private String support_action_taken;
	
	@Column(name = "hod_telephone")
	private String 	hod_telephone;
	
	@Column(name = "datetime")
	private String 	datetime;
	
}
