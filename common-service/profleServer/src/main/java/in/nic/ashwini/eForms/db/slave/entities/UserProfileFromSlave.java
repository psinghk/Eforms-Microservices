package in.nic.ashwini.eForms.db.slave.entities;

import java.time.LocalDateTime;

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
@Data
public class UserProfileFromSlave {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String mobile;
	private String name;
	private String initials;
	private String fname;
	private String mname;
	private String lname;
	
	@Column(name = "ophone")
	private String officePhone;
	private String altophone;
	private String intercom;
	private String ipphone;
	
	@Column(name = "rphone")
	private String residencePhone;
	private String altrphone;
	private String emergency_contact;
	private String designation;
	
	@Column(name = "emp_code")
	private String empCode;
	private String dob;
	private String dor;
	private String room;
	private String bay;
	private String floor;
	private String building;
	private String block;
	private String area;
	
	private String address;
	private String city;
	
	@Column(name = "add_state")
	private String postingState;
	
	private String pin;
	private String employment;
	private String ministry;
	private String department;
	
	@Column(name = "other_dept")
	private String otherDepartment;
	
	private String state;
	private String organization;
	
	@Column(name = "ca_name")
	private String roName;
	
	@Column(name = "ca_desig")
	private String roDesignation;
	
	@Column(name = "ca_email")
	private String roEmail;
	
	@Column(name = "ca_mobile")
	private String roMobile;
	
	@Column(name = "hod_name")
	private String hodName;
	
	@Column(name = "hod_telephone")
	private String hodTelephone;
	
	@Column(name = "hod_email")
	private String hodEmail;
	
	@Column(name = "hod_mobile")
	private String hodMobile;
	
	@Column(name = "hod_designation")
	private String hodDesignation;
	
	@Column(name = "userip")
	private String ip;
	
	@CreationTimestamp
	@Column(name = "datetime")
	private LocalDateTime creationTimeStamp;
	
	//@Column(name = "last_updated")
	//private LocalDateTime updationTimeStamp;
	
	@Column(name = "user_alt_address")
	private String alternateEmailAddress;
	
	@Column(name = "under_sec_name")
	private String usName;
	
	@Column(name = "under_sec_email")
	private String usEmail;
	
	@Column(name = "under_sec_mobile")
	private String usMobile;
	
	@Column(name = "under_sec_desig")
	private String usDesignation;
	
	@Column(name = "under_sec_telephone")
	private String usTelephone;
	private String nic_employee;
	private String nic_coordinator;
	private String user_status;
	private char flag;
	private char exist_user;
}
