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
@Table(name = "employment_coordinator")
@Access(value = AccessType.FIELD)
@Data
public class MobileEmpCoord {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long emp_id;
	@Column(name = "emp_category")
	private String 	category;
	
	@Column(name = "emp_min_state_org")
	private String 	emp_min_state_org;
	
	@Column(name = "emp_dept")
	private String 	empDept;
	
	@Column(name = "emp_domain")
	private String domain;
	
	@Column(name = "emp_bo_id")
	private String boId;
	
	@Column(name = "emp_coord_mobile")
	private String coordMobile;
	
	@Column(name = "emp_coord_name")
	private String coordName;
	
	@Column(name = "emp_coord_email")
	private String coordEmail;
	
	@Column(name = "emp_admin_email")
	private String adminEmail;
	
	@Column(name = "emp_type")
	private String 	empType;
	
	@Column(name = "emp_status")
	private String empStatus;
	
	@Column(name = "ip")
	private String 	ip;
	
	@Column(name = "emp_mail_acc_cat")
	private String emp_mail_acc_cat;
}
