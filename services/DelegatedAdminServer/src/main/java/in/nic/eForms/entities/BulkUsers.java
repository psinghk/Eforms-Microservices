package in.nic.eForms.entities;


import java.sql.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

//need to create table in dto for this.

@Entity
@Table(name="bulk_users")
@Access(value = AccessType.FIELD)
@Data
public class BulkUsers {
	@Id
	@Column(name="bulk_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int bulkid; //need to check the registration no.
	//registration_no
	@Column(name="registration_no")
	private String registrationNo;
	
	@Column(name="form_type")
	private String form_type="email_bulk";// setting a default value
	

	private String fname;
	private String lname;
	private String designation;   //user_design;
	private String department;   //dept;
	private String state;           //user_state;
	private String mobile;         //user_mobile;
	private String dor;           //single_dor;
	private String uid;
	private String mail;           //                 
	private String dob;          // single_dob;   //varchar(20)
	
	@Column(name="emp_code")
	private String empcode;        // user_empcode; //varchar(20)
	
	private String createdon;         // timestamp
	
	//@Temporal(TemporalType.TIMESTAMP)
	//@Column(name="updatedon", nullable=false, updatable=false)
	private Date updatedon;    //datetime
	
	@Column(name="is_created")  //char(1)
	private String iscreated;
	
	@Column(name="is_rejected")  //char(1)
	private String isrejected;
	
	@Column(name="reject_remarks") //text
	private String rejectremarks;
	
	@Column(name="uid_assigned")  //varchar(50)
	private String uidassigned;
	
	@Column(name="mail_assigned")  //varchar(100)
	private String mailassigned;
	
	@Column(name="acc_cat")   //varchar(20)
	private String acccat;
	
	@Column(name="rejected_by") //varchar(255)
	private String rejectedby; 
	
	
	@Column(name="update_flag") //char(10)
	private String updateflag;
	
	@Column(name="updated_by")  //varchar(255)
	private String updatedby;
	
	
	@Column(name="allow_creation") //char(1)
	private String allowcreation;


	


	

}
