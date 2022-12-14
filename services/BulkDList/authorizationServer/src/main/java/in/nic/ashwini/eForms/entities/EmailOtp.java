package in.nic.ashwini.eForms.entities;

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
@Table(name = "email_otp")
@Data
public class EmailOtp {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer id;
	
	public String email;
	public Integer otp;
	
	
	@Column(name = "resend_attempt")
	public Integer resendAttempt;
	
	@Column(name = "ip_address")
	public String ip;
	
	@Column(name = "attempt_login")
	public Integer numberOfLoginAttempts;
	
	@Column(name = "generationtime")
	public LocalDateTime generationTimeStamp;
	
	@Column(name = "expirytime")
	public LocalDateTime expiryTimeStamp;

}
