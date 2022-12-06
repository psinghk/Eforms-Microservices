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
@Table(name="aliasadded")
public class AddAliasTrailBean {

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;

	@Column(name="login_id")
	private String login_id;

	@Column(name="remote_ip")
	private String remote_ip;
	
	@Column(name="user_id")
	private String uid;

	@Column(name="add_equivalentaddress	")
	private String add_equivalentaddress;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name="date_time")
	private LocalDateTime datetime;

	@Column(name="sms_mid")
	private String sms_mid;

	@Column(name="sms_mobile")
	private String sms_mobile;

	@Column(name="da_mail")
	private String da_mail;

	@Column(name="da_mobile")
	private String da_mobile;

	public AddAliasTrailBean(String login_id, String remote_ip, String uid, String add_equivalentaddress, LocalDateTime datetime, String sms_mid, String sms_mobile, String da_mail, String da_mobile) {
		super();
		this.login_id = login_id;
		this.remote_ip = remote_ip;
		this.uid = uid;
		this.add_equivalentaddress = add_equivalentaddress;
		this.datetime = datetime;
		this.sms_mid = sms_mid;
		this.sms_mobile = sms_mobile;
		this.da_mail = da_mail;
		this.da_mobile = da_mobile;
	}

	
	
}
