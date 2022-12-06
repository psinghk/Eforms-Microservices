package in.nic.ashwini.eForms.db.master.entities;

import java.io.Serializable;
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
@Table(name = "query_raise")
@Data
public class Query implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "qr_id")
	private Long id;
	@Column(name = "qr_reg_no")
	private String registrationNo;
	@Column(name = "qr_forwarded_by")
	private String senderType;
	@Column(name = "qr_forwarded_by_user")
	private String sender;
	@Column(name = "qr_forwarded_to")
	private String recipientType;
	@Column(name = "qr_forwarded_to_user")
	private String recipient;
	@Column(name = "qr_message")
	private String query;
	@CreationTimestamp
	@Column(name = "qr_createdon")
	private LocalDateTime queryRaisedTime;
	@Column(name= "qr_form_type")
	private String 	formType;
}
