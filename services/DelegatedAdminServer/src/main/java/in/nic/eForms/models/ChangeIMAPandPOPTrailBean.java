package in.nic.eForms.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name="pisupdate")
public class ChangeIMAPandPOPTrailBean {

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;

	@Column(name="login_id")
	private String login_id;

	@Column(name="remote_ip")
	private String remote_ip;

	@Column(name="user_id")
	private String uid;
	
	@Column(name="old_value")
	private String old_value;

	@Column(name="new_value")
	private String new_value;
	
	@Column(name="action")
	private String action;

	@Column(name="sms_mid")
	private String sms_mid;
    
	@Column(name="sms_mobile")
	private String sms_mobile;

	@Column(name="da_email")
	private String da_email;

	@Column(name="da_mobile")
	private String da_mobile;
	
	@Column(name="filename")
	private String filename;
    
	@Column(name="remark")
	private String remark;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name="date_time")
	private LocalDateTime datetime;
    
	@Column(name="ip")
	private String ip;
	
	@Column(name="user_mail")
	private String user_mail;
	
	@Column(name="user_mobile")
	private String user_mobile;

	public ChangeIMAPandPOPTrailBean(String login_id, String remote_ip, String user_id, String old_value,
			String new_value, String action, String sms_mid, String sms_mobile, String da_email, String da_mobile,
			String filename, String remark, LocalDateTime datetime, String ip, String user_mail, String user_mobile) {
		super();
		this.login_id = login_id;
		this.remote_ip = remote_ip;
		this.uid = user_id;
		this.old_value = old_value;
		this.new_value = new_value;
		this.action = action;
		this.sms_mid = sms_mid;
		this.sms_mobile = sms_mobile;
		this.da_email = da_email;
		this.da_mobile = da_mobile;
		this.filename = filename;
		this.remark = remark;
		this.datetime = datetime;
		this.ip = ip;
		this.user_mail = user_mail;
		this.user_mobile = user_mobile;
	}
		
}
