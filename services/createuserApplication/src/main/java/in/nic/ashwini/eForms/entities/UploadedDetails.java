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
@Table(name = "uploaded_details")
public class UploadedDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "uid")
	private String uid;
	@Column(name = "fname")
	private String firstname;
	@Column(name = "lname")
	private String lastname;
	@Column(name = "password")
	private String password;
	@Column(name = "description")
	private String description;
	@Column(name = "mobile")
	private String mobile;
	@Column(name = "dob")
	private String dateofbirth;
	@Column(name = "dor")
	private String dateofretirement;
	@Column(name = "desig")
	private String designation;
	@Column(name = "dept")
	private String department;
	@Column(name = "state")
	private String state;
	@Column(name = "empcode")
	private String empcode;
	@Column(name = "mail")
	private String email;
	@Column(name = "mailequivalentaddresss")
	private String mailequivalentaddress;
	@Column(name = "filename")
	private String filename;
	@Column(name = "datetime")
	private Timestamp datetime;

}
