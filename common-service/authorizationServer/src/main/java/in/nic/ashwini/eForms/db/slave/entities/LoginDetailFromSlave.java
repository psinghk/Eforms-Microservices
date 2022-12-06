package in.nic.ashwini.eForms.db.slave.entities;

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
@Table(name = "login_details")
@Data
public class LoginDetailFromSlave {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String email;
	private String ip;
	private String role;
	private String remarks;
	@Column(name="session_id")
	private String sessionId;
	
	@Column(name="logout_time")
	private LocalDateTime logoutTime;
	
	@CreationTimestamp
	@Column(name="login_time")
	private LocalDateTime loginTime;
	
	@Column(name = "logout_status")
	private String logoutStatus;
	
	private Integer status;
}
