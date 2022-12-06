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
@Table(name = "create_user_app_auth")
@Data
public class CreateUserAuth {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String email;
	private String ip;
	private Integer status;
	
	@Column(name = "created_time")
	private LocalDateTime creationTimeStamp;
}
