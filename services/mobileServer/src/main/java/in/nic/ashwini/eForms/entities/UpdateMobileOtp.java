package in.nic.ashwini.eForms.entities;

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
@Table(name = "update_mobile_otp")
@Access(value = AccessType.FIELD)
@Data
public class UpdateMobileOtp {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name = "otp")
	private int otp;
	
	@Column(name= "mobile")
	private String mobile;

	
	@Column(name = "gentime")
	public LocalDateTime generationTimeStamp;
	
	@Column(name = "exptime")
	public LocalDateTime expiryTimeStamp;

	

}
