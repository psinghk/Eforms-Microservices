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
@Table(name = "error_login_details")
@Data
public class ErrorLoginDetail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String email;
	private String ip;
	private String role;
	private String remarks;
	
	@CreationTimestamp
	@Column(name="login_time")
	private LocalDateTime loginTime;
}
