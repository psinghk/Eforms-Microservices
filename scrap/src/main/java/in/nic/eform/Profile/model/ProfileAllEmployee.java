package in.nic.eform.Profile.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "user_profile")
@Data
public class ProfileAllEmployee {
	@Id
	@Column(name = "email")
	private String authEmail;
	@Column
	private String mobile;
	@Column
	private String name;
	@Column
	private String designation;
	@Column
	private String empCode;
	@Column
	private String address;
	@Column
	private String city;
	@Column
	private String addState;
	@Column
	private String pin;
	@Column
	private String employment;
	@Column
	private String ministry;
	@Column
	private String department;
	@Column
	private String otherDept;
	@Column
	private String state;
	@Column
	private String organization;
	@Column
	private String hodName;
	@Column
	private String hodEmail;
	@Column
	private String hodMobile;
	@Column
	private String hodDesig;
	@Column
	private String hodTelephone;
	@Column
	private String userip;
	@Column
	private Timestamp datetime;
	@Column
	private String underSecName;
	@Column
	private String underSecDesig;
	@Column
	private String underSecMobile;
	@Column
	private String underSecEmail;
	@Column
	private String underSecTelephone;
	
}
