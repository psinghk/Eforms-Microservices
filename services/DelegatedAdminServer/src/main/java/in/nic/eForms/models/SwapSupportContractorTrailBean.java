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
@Table(name="domain_changed_audit")
public class SwapSupportContractorTrailBean {

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;

	@Column(name="da_admin")
	private String da_login_id;

	@Column(name="da_email")
	private String da_mail;

	@Column(name="user_email")
	private String user_mail;
	
	@Column(name="uid")
	private String uid;

	@Column(name="action")
	private String action;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name="date_time")
	private LocalDateTime datetime;
	
	@Column(name="prev_primary")
	private String prev_primary;

	@Column(name="curr_primary")
	private String curr_primary;
    
	@Column(name="mid")
	private String sms_mid;

	@Column(name="bo")
	private String bo_name;

	public SwapSupportContractorTrailBean(String da_login_id, String da_mail, String user_mail, String uid,
			String action, LocalDateTime datetime, String prev_primary, String curr_primary, String sms_mid,
			String bo_name) {
		super();
		this.da_login_id = da_login_id;
		this.da_mail = da_mail;
		this.user_mail = user_mail;
		this.uid = uid;
		this.action = action;
		this.datetime = datetime;
		this.prev_primary = prev_primary;
		this.curr_primary = curr_primary;
		this.sms_mid = sms_mid;
		this.bo_name = bo_name;
	}

	
	
}
