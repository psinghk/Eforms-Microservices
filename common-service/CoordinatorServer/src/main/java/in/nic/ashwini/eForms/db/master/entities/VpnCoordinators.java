package in.nic.ashwini.eForms.db.master.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Data
@Table(name = "vpn_coordinator")
public class VpnCoordinators {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_id")
	public Integer id;
	
	@Column(name = "emp_category")
	public String employmentCategory;
	
	@Column(name = "emp_min_state_org")
	public String ministry;
	
	@Column(name = "emp_dept")
	public String department;
	
	@Column(name = "emp_sub_dept")
	public String subDepartment;
	
	@Column(name = "emp_coord_email")
	public String email;
	
	@Column(name = "emp_coord_name")
	public String name;
	
	@Column(name = "emp_coord_mobile")
	public String mobile;
	
	@Column(name = "emp_coord_desig")
	public String designation;
	
	@Column(name = "emp_admin_email")
	public String adminEmail;
	
	@Column(name = "emp_status")
	public String status;
	
	@CreationTimestamp
	@Column(name = "emp_createdon")
	public LocalDateTime createdOn;
	
	@Column(name = "emp_addedby")
	public String addedBy;
	
	@Column(name = "ip")
	public String vpnIp;
}
