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
@Table(name = "support_app_auth")
@Data
public class SupportAppAuth {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "ip")
	private String ip;
	
	@Column(name = "status")
	private Integer status;
	
	@CreationTimestamp
	@Column(name = "create_time")
	private LocalDateTime creationTimeStamp;

	
}
