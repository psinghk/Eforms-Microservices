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
@Table(name="passwordreset")
public class ResetPasswordTrailBean {

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;
	
	@Column(name="login_id")
	private String uid;

	@Column(name="remote_ip")
	private String remote_ip;

	@Column(name="user_id")
	private String user_id;

	@Column(name="old_mobile")
	private String old_mobile;

	@Column(name="new_mobile")
	private String new_mobile;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name="date_time")
	private LocalDateTime datetime;

	@Column(name="sms_mid")
	private String sms_mid;

	@Column(name="da_email")
	private String da_mail;

	@Column(name="da_mobile")
	private String da_mobile;
	
	@Column(name="filename")
	private String filename;

	public ResetPasswordTrailBean(String login_id, String remote_ip, String user_id, String old_mobile,
			String new_mobile, LocalDateTime datetime, String sms_mid, String da_mail, String da_mobile,
			String filename) {
		super();
		this.uid = login_id;
		this.remote_ip = remote_ip;
		this.user_id = user_id;
		this.old_mobile = old_mobile;
		this.new_mobile = new_mobile;
		this.datetime = datetime;
		this.sms_mid = sms_mid;
		this.da_mail = da_mail;
		this.da_mobile = da_mobile;
		this.filename = filename;
	}
	
	

}
