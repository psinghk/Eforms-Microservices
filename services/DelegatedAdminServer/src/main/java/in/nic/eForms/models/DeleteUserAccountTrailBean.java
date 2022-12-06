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
@Table(name="delete_user")
public class DeleteUserAccountTrailBean {

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;
	
	@Column(name="login_id")
	private String uid;

	@Column(name="remote_ip")
	private String remote_ip;

	@Column(name="user_id")
	private String user_id;

	@Column(name="user_mail")
	private String user_mail;

	@Column(name="user_mobile")
	private String user_mobile;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name="date_time")
	private LocalDateTime datetime;

	@Column(name="da_email")
	private String da_mail;

	@Column(name="da_mobile")
	private String da_mobile;

	public DeleteUserAccountTrailBean(String uid, String remote_ip, String user_id, String user_mail,
			String user_mobile, LocalDateTime datetime, String da_mail, String da_mobile) {
		super();
		this.id = id;
		this.uid = uid;
		this.remote_ip = remote_ip;
		this.user_id = user_id;
		this.user_mail = user_mail;
		this.user_mobile = user_mobile;
		this.datetime = datetime;
		this.da_mail = da_mail;
		this.da_mobile = da_mobile;
	}

	
	
}
