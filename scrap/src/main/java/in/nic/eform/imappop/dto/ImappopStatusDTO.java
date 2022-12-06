package in.nic.eform.imappop.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "status")
@Data
public class ImappopStatusDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

//	@Id
//	@Column(name = "id")
//	private long id;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "stat_id")
	private int statid;
	@Column(name = "stat_reg_no")
	private String statregno;
	@Column(name = "stat_type")
	private String stat_type;
	@Column(name = "stat_forwarded_by")
	private String statforwardedby;
	@Column(name = "stat_forwarded_by_user")
	private String stat_forwarded_by_user;
	@Column(name = "stat_forwarded_to")
	private String statforwardedto;
	@Column(name = "stat_forwarded_to_user")
	private String stat_forwarded_to_user;
	@Column(name = "stat_remarks")
	private String stat_remarks;
	@Column(name = "stat_forwarded_by_email")
	private String stat_forwarded_by_email;
	@Column(name = "stat_forwarded_by_mobile")
	private String stat_forwarded_by_mobile;
	@Column(name = "stat_forwarded_by_name")
	private String stat_forwarded_by_name;
	@Column(name = "stat_forwarded_by_datetime")
	private String stat_forwarded_by_datetime;
	@Column(name = "stat_createdon")
	private String stat_createdon;
	@Column(name = "stat_on_hold")
	private String stat_on_hold;
	@Column(name= "stat_process")
	private String 	stat_process;
	@Column(name= "stat_form_type")
	private String 	stat_form_type;
	@Column(name= "stat_ip")
	private String 	statip;
	@Column(name= "stat_forwarded_by_ip")
	private String 	stat_forwarded_by_ip;
	@Column(name= "stat_final_id")
	private String stat_final_id;
	
	
	
}
