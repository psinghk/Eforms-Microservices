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
@Table(name="userstatusecord")
public class UpdateDateOfExpiryTrailBean {

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;
	
	@Column(name="login_id")
	private String uid;

	@Column(name="remote_ip")
	private String remote_ip;

	@Column(name="user_id")
	private String user_id;
	
	@Column(name="old_mailstatus")
	private String old_mailstatus;
	
	@Column(name="old_inetstatus")
	private String old_inetstatus;
	
	@Column(name="updated_mailstatus")
	private String updated_mailstatus;
	
	@Column(name="updated_inetstatus")
	private String updated_inetstatus;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name="date_time")
	private LocalDateTime datetime;
	
	@Column(name="remarks")
	private String remarks;
	
	@Column(name="sms_mobile")
	private String sms_mobile;
	
	@Column(name="old_exp_dor")
	private String old_exp_dor;

	@Column(name="sms_mid")
	private String sms_mid;

	@Column(name="da_email")
	private String da_mail;

	@Column(name="da_mobile")
	private String da_mobile;
	
	@Column(name="filename")
	private String filename;
	
	@Column(name="dor_remark")
	private String dor_remarks;

	public UpdateDateOfExpiryTrailBean(String uid, String remote_ip, String user_id, String old_mailstatus,
			String old_inetstatus, String updated_mailstatus, String updated_inetstatus, LocalDateTime datetime,
			String remarks, String sms_mobile, String old_exp_dor, String sms_mid, String da_mail, String da_mobile,
			String filename, String dor_remarks) {
		super();
		this.uid = uid;
		this.remote_ip = remote_ip;
		this.user_id = user_id;
		this.old_mailstatus = old_mailstatus;
		this.old_inetstatus = old_inetstatus;
		this.updated_mailstatus = updated_mailstatus;
		this.updated_inetstatus = updated_inetstatus;
		this.datetime = datetime;
		this.remarks = remarks;
		this.sms_mobile = sms_mobile;
		this.old_exp_dor = old_exp_dor;
		this.sms_mid = sms_mid;
		this.da_mail = da_mail;
		this.da_mobile = da_mobile;
		this.filename = filename;
		this.dor_remarks = dor_remarks;
	}	
	
	

}
