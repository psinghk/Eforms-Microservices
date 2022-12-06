package in.nic.ashwini.eForms.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "rejected_user")
public class RejectedUser {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id; 
	@Column(name = "user_type")
	private String userType;
	private String uid;
	private Timestamp datetime;
	@Column(name = "reject_reason")
	private String rejectReason;
	private String ip;
	private String remarks;
	
}
