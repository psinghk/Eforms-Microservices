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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Data;

@Entity
@Table(name = "status")
@Data
public class Status implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "stat_id")
	private Long id;
	@Column(name = "stat_reg_no")
	private String registrationNo;
	@Column(name = "stat_type")
	private String status;
	@Column(name = "stat_forwarded_by")
	private String senderType;
	@Column(name = "stat_forwarded_by_user")
	private String sender;
	@Column(name = "stat_forwarded_to")
	private String recipientType;
	@Column(name = "stat_forwarded_to_user")
	private String recipient;
	@Column(name = "stat_remarks")
	private String remarks;
	@Column(name = "stat_forwarded_by_email")
	private String senderEmail;
	@Column(name = "stat_forwarded_by_mobile")
	private String senderMobile;
	@Column(name = "stat_forwarded_by_name")
	private String senderName;
	@CreationTimestamp
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	@Column(name = "stat_forwarded_by_datetime")
	private LocalDateTime senderDatetime;
	@CreationTimestamp
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	@Column(name = "stat_createdon")
	private LocalDateTime createdon;
	@Column(name = "stat_on_hold")
	private String onholdStatus;
	@Column(name= "stat_process")
	private String 	submissionType;
	@Column(name= "stat_form_type")
	private String 	formType;
	@Column(name= "stat_ip")
	private String 	ip;
	@Column(name= "stat_forwarded_by_ip")
	private String 	senderIp;
	@Column(name= "stat_final_id")
	private String finalId;
}
