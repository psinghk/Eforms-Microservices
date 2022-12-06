package in.nic.ashwini.eForms.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "wifi_registration")
@Data
public class GeneratePdf {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	@Column(name = "registration_no")
	private String registrationNo;
	@Column(name = "auth_off_name")
	private String applicantName;
	@Column(name = "auth_email")
	private String applicantEmail;
	@Column(name = "ministry")
	private String ministry;
	@Column(name = "mobile")
	private String applicantMobile;
	@Column(name = "hod_name")
	private String hodName;
	@Column(name = "hod_email")
	private String hodEmail;
	@Column(name = "hod_mobile")
	private String hodMobile;
	
	
	
}
