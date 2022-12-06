package in.nic.ashwini.eForms.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "email_creation_trail")
public class EmailCreationTrail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="created_email")
	private String createdEmail;
	
	@Column(name="date_of_creation")
	private Timestamp dateOfCreation;
	
	@Column(name="date_of_expiry")
	private Timestamp dateOfExpiry;
	
	@Column(name="creater_email")
	private String createrEmail;
	
	private String ip;
	
	@Column(name = "form_type")
	private  String formType;

	
	
	
}
