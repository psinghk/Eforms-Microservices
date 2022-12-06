package in.nic.eform.relayip.dto;

import java.io.Serializable;

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
@Table(name = "query_raise")
@Access(value = AccessType.FIELD)
@Data
public class RelayIPQueryRaiseDTO implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int qr_id;
	
	@Column(name = "qr_form_type")
	private String qr_form_type;
	
	@Column(name = "qr_reg_no")
	private String qrregno;
	
	@Column(name = "qr_forwarded_by")
	private String qr_forwarded_by;
	
	@Column(name = "qr_forwarded_by_user")
	private String qr_forwarded_by_user;
	
	@Column(name = "qr_forwarded_to")
	private String qr_forwarded_to;
	
	@Column(name = "qr_forwarded_to_user")
	private String qr_forwarded_to_user;
	
	@Column(name = "qr_message")
	private String qr_message;
	
	@Column(name = "qr_createdon")
	private String qr_createdon;

}
