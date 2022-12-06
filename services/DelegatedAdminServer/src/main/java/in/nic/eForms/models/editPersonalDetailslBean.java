package in.nic.eForms.models;

import java.sql.Date;
import java.time.LocalDateTime;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name="userprofile")
public class editPersonalDetailslBean {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;
	 
	@Column(name="login_id")
	private String loginId;

	@Column(name="uid")
	private String uid;
	
	@Column(name="oldnew_fname")
	private String oldnew_fname;
	
	@Column(name="oldnew_mname")
	private String oldnew_mname;
	
	@Column(name="oldnew_lname")
	private String oldnew_lname;

	@Column(name="oldnew_mobile")
	private String oldnew_Mobile;

	@Column(name="oldnew_telephone")
	private String oldnew_telephone;
	
	@Column(name="oldnew_designation")
	private String oldnew_designation;

	@Column(name="oldnew_department")
	private String oldnew_department;
	
	@Column(name="oldnew_address")
	private String oldnew_address;
	
	@Column(name="oldnew_empnum")
	private String oldnew_empnum;

	@Column(name="remote_ip")
	private String remoteIp;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name="DateTime")
	private LocalDateTime datetime;
		
	@Column(name="sms_mid")
	private String smsMid;
	
	@Column(name="sms_mobile")
	private String sms_mobile;
	
	@Column(name="da_email")
	private String da_mail;
	
	@Column(name="da_mobile")
	private String da_mobile;

	public editPersonalDetailslBean(String loginId, String uid, String oldnew_fname, String oldnew_mname,
			String oldnew_lname, String oldnew_Mobile, String oldnew_telephone, String oldnew_designation,
			String oldnew_department, String oldnew_address, String oldnew_empnum, String remoteIp,
			LocalDateTime datetime, String smsMid, String sms_mobile, String da_mail, String da_mobile) {
		super();
		this.loginId = loginId;
		this.uid = uid;
		this.oldnew_fname = oldnew_fname;
		this.oldnew_mname = oldnew_mname;
		this.oldnew_lname = oldnew_lname;
		this.oldnew_Mobile = oldnew_Mobile;
		this.oldnew_telephone = oldnew_telephone;
		this.oldnew_designation = oldnew_designation;
		this.oldnew_department = oldnew_department;
		this.oldnew_address = oldnew_address;
		this.oldnew_empnum = oldnew_empnum;
		this.remoteIp = remoteIp;
		this.datetime = datetime;
		this.smsMid = smsMid;
		this.sms_mobile = sms_mobile;
		this.da_mail = da_mail;
		this.da_mobile = da_mobile;
	}
	
	

}
