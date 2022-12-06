package in.nic.eform.imappop.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "imappop_registration")
@Data
public class GeneratePdfBaseDTO {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	@Column(name = "registration_no")
	private String registrationno;
	@Column(name = "auth_off_name")
	private String applicant_name;
	@Column(name = "auth_email")
	private String applicant_email;
	@Column(name = "description")
	private String description;
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
	
}
