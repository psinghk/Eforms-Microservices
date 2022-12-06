package in.nic.ashwini.eForms.db.master.entities;

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
@Table(name = "error_otp_details")
@Data
public class ErrorOtpDetail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String email;
	private String mobile;
	private String ip;
	private String role;
	private String remarks;
	@Column(name="invalid_otp")
	private String invalidOtp;
	
	@CreationTimestamp
	@Column(name="login_time")
	private LocalDateTime loginTime;
}
