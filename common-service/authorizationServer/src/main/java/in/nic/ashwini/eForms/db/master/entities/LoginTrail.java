package in.nic.ashwini.eForms.db.master.entities;

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
@Table(name = "login_trail")
@Data
public class LoginTrail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	
	@Column(name = "service_name")
	public String serviceName;
	
	public String email;
	public String mobile;
	public String role;
	public String token;
	public String ip;
	
	@CreationTimestamp
	@Column(name = "login_time")
	public LocalDateTime loginTime;
	
	@Column(name = "logout_time")
	public LocalDateTime logoutTime;

	@Column(name = "login_remarks")
	public String loginRemarks;
	
	@Column(name = "logout_remarks")
	public String logoutRemarks;
	
	public Integer status = 0;
}
