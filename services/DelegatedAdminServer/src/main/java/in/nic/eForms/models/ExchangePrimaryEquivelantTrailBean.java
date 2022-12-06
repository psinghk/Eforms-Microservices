package in.nic.eForms.models;

import java.time.LocalDateTime;
import java.util.List;

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
@Table(name="primaryadded")
public class ExchangePrimaryEquivelantTrailBean {

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;

	@Column(name="login_id")
	private String login_id;

	@Column(name="remote_ip")
	private String remote_ip;
	
	@Column(name="uid")
	private String uid;

	@Column(name="old_primary")
	private String old_primary;

	@Column(name="new_primary")
	private String new_primary;

	@Column(name="old_alias")
	private String old_alias;

	@Column(name="new_alias")
	private String new_alias;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name="date_time")
	private LocalDateTime datetime;

	@Column(name="sms_mid")
	private String sms_mid;

	@Column(name="sms_mobile")
	private String sms_mobile;

	@Column(name="da_email")
	private String da_email;
	
	@Column(name="da_mobile")
	private String da_mobile;

	public ExchangePrimaryEquivelantTrailBean(String login_id, String remote_ip, String uid, String old_primary, String new_primary, String string, String string2, 
			LocalDateTime datetime, String sms_mid, String sms_mobile, String da_email, String da_mobile) {
		super();
		this.login_id = login_id;
		this.remote_ip = remote_ip;
		this.uid = uid;
		this.old_primary = old_primary;
		this.new_primary = new_primary;
		this.old_alias = string;
		this.new_alias = string2;
		this.datetime = datetime;
		this.sms_mid = sms_mid;
		this.sms_mobile = sms_mobile;
		this.da_email = da_email;
		this.da_mobile = da_mobile;
	}

	
	
}
