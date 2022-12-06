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
@Table(name = "created_user")
public class CreatedUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_type")
	private String userType;

	private String uid;
	private Timestamp datetime;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_by_name")
	private String createdByName;
	
	@Column(name="date_of_creation")
	private Timestamp dateOfCreation;

	private String ip;
	private String bodn;
	private String remarks;

}
