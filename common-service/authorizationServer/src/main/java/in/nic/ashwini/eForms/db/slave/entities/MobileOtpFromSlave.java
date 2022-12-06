package in.nic.ashwini.eForms.db.slave.entities;

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
@Table(name = "mobile_otp")
@Data
public class MobileOtpFromSlave {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer id;
	
	public String mobile;
	public Integer otp;
	
	@Column(name = "resend_attempt")
	public Integer resendAttempt;
	
	@Column(name = "ip_address")
	public String ip;
	
	@Column(name = "attempt_login")
	public Integer numberOfLoginAttempts;
	
	@CreationTimestamp
	@Column(name = "generationtime")
	public LocalDateTime generationTimeStamp;
	
	@Column(name = "expirytime")
	public LocalDateTime expiryTimeStamp;

}
