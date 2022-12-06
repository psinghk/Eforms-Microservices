package in.nic.eForms.entities;

import java.time.LocalDateTime;

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
@Table(name = "user_profile")
@Access(value = AccessType.FIELD)
@Data
public class UserProfile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "admin_email")
	private String adminEmail;

	@Column(name = "admin_id") 
	private String adminId;
	
	@Column(name = "admin_ip") 
	private String adminIp;

	@Column(name = "admin_mobile")
	private String adminMobile;
	
	@Column(name="date_time")
	private String dateTime;
	
	@Column(name="department")
	private String departmnt;
	
	@Column(name = "designation")
	private String designtn;

	@Column(name = "employee_ode")
	private String employeeCode;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "middle_name")
	private String middleName;
	
	@Column(name = "mobile")
	private String mob;
	
	@Column(name = "address")
	private String Addess;

    @Column(name = "sms_mid")
	private String smsMid;
	
	@Column(name = " sms_mobile")
	private String smsMobile;
		
	@Column(name = "telephone_number")
	private String telephoneNumber;
	
	@Column(name = "uid")
	private String uid;

}
