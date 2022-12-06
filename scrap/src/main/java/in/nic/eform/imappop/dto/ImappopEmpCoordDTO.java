package in.nic.eform.imappop.dto;

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
public class ImappopEmpCoordDTO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String 	emp_id;
	@Column(name = "emp_category")
	private String 	empcategory;
	
	@Column(name = "emp_min_state_org	")
	private String 	empminstateorg;
	
	@Column(name = "emp_dept")
	private String 	empdept;
	
	@Column(name = "emp_sub_dept")
	private String 	emp_sub_dept;
	
	@Column(name = "emp_mail_acc_cat")
	private String 	emp_mail_acc_cat;
	
	@Column(name = "emp_sms_acc_cat")
	private String 	emp_sms_acc_cat;
	
	@Column(name = "emp_domain")
	private String 	emp_domain;
	
	@Column(name = "emp_bo_id")
	private String 	emp_bo_id;
	
	@Column(name = "emp_coord_name")
	private String 	emp_coord_name;
	
	@Column(name = "emp_coord_email")
	private String 	empcoordemail;
	
	@Column(name = "emp_admin_email")
	private String 	empadminemail;
	
	@Column(name = "emp_status")
	private String empstatus;
	
	@Column(name = "emp_createdon")
	private String emp_createdon;
	
	@Column(name = "emp_addedby")
	private String emp_addedby;
	
	@Column(name = "ip")
	private String ip;
}
