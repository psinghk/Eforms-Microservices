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
@Table(name="retiredbomoverecord")
public class MoveToRetiredBOTrailBean {

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;

	@Column(name="da_email")
	private String da_mail;

	@Column(name="da_mobile")
	private String da_mobile;
	
	@Column(name="user_dn")
	private String user_dn;

	@Column(name="new_dn")
	private String new_dn;

	@Column(name="uid")
	private String uid;

	@Column(name="old_po")
	private String old_po;

	@Column(name="old_bo")
	private String old_bo;

	@Column(name="remarks")
	private String remarks;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name="date_time")
	private LocalDateTime datetime;

	public MoveToRetiredBOTrailBean(String da_mail, String da_mobile, String user_dn, String new_dn, String uid,
			String old_po, String old_bo, String remarks, LocalDateTime datetime) {
		super();
		this.da_mail = da_mail;
		this.da_mobile = da_mobile;
		this.user_dn = user_dn;
		this.new_dn = new_dn;
		this.uid = uid;
		this.old_po = old_po;
		this.old_bo = old_bo;
		this.remarks = remarks;
		this.datetime = datetime;
	}	
	
}
