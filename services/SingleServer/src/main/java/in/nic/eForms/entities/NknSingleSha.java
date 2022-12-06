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

import lombok.Data;

@Entity
@Table(name = "sha_save")
@Access(value = AccessType.FIELD)
@Data
public class NknSingleSha {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long sha_id;
	@Column(name = "sha_reg_no")
	private String registrationNo;
	
	@Column(name = "sha_form_type")
	private String form_type;
	
	@Column(name = "sha_ro_email")
	private String ro_email;
	
	@Column(name = "sha_us_email")
	private String us_email;
	
	@Column(name = "sha_ip")
	private String ip;
	
	@Column(name = "sha_value")
	private String sha_value;
	
	@Column(name = "sha_status")
	private String sha_status;
	
	@Column(name = "sha_ro_datetime")
	private LocalDateTime sha_ro_datetime;
	
	@Column(name = "sha_us_datetime")
	private LocalDateTime sha_us_datetime;
	
	@Column(name = "sha_otp")
	private String sha_otp;
	
	@Column(name = "sha_otp_datetime")
	private LocalDateTime otp_datetime;
	
	
	
}
