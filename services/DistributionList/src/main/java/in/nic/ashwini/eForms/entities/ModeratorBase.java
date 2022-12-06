package in.nic.ashwini.eForms.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "dlist_moderator")
@Access(value = AccessType.FIELD)
@Data
public class ModeratorBase {

	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	@Column(name = "name")
	private String omName;
	@Column(name = "mobile")
	private String omMobile;
	@Column(name = "email")
	private String omEmail;
	
	@Column(name = "form_type")
	private String formType;

}

