package in.nic.ashwini.eForms.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Data;

@Entity
@Table(name = "final_audit_track")
@Access(value = AccessType.FIELD)
@Data
public class FinalAuditTrack implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	
	@Column(name = "applicant_email")
	private String applicantEmail;
	@Column(name = "applicant_mobile")
	private String applicantMobile;
	@Column(name = "applicant_name")
	private String applicantName;
	@Column(name = "applicant_ip")
	private String applicantIp;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	@Column(name = "applicant_datetime")
	private LocalDateTime applicantDatetime;
	@Column(name = "applicant_remarks")
	private String applicantRemarks;
	
	@Column(name = "to_email")
	private String toEmail;
	@Column(name = "to_name")
	private String toName;
	@Column(name = "to_mobile")
	private String toMobile;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	@Column(name = "to_datetime")
	private LocalDateTime toDatetime;
	@Column(name = "status")
	private String status;
	@Column(name = "form_name")
	private String formName;
	
	@Column(name = "ca_email")
	private String caEmail;
	@Column(name = "ca_mobile")
	private String caMobile;
	@Column(name = "ca_name")
	private String caName;
	@Column(name = "ca_ip")
	private String caIp;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	@Column(name = "ca_datetime")
	private LocalDateTime caDatetime;
	@Column(name = "ca_remarks")
	private String caRemarks;
	
	@Column(name = "us_email")
	private String usEmail;
	@Column(name = "us_mobile")
	private String usMobile;
	@Column(name = "us_name")
	private String usName;
	@Column(name = "us_ip")
	private String usIp;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	@Column(name = "us_datetime")
	private LocalDateTime usDatetime;
	@Column(name = "us_remarks")
	private String usRemarks;
	
	@Column(name = "coordinator_email")
	private String coordinatorEmail;
	@Column(name = "coordinator_mobile")
	private String coordinatorMobile;
	@Column(name = "coordinator_name")
	private String coordinatorName;
	@Column(name = "coordinator_ip")
	private String coordinatorIp;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	@Column(name = "coordinator_datetime")
	private LocalDateTime coordinatorDatetime;
	@Column(name = "coordinator_remarks")
	private String coordinatorRemarks;
	
	@Column(name = "support_email")
	private String supportEmail;
	@Column(name = "support_mobile")
	private String supportMobile;
	@Column(name = "support_name")
	private String supportName;
	@Column(name = "support_ip")
	private String supportIp;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	@Column(name = "support_datetime")
	private LocalDateTime supportDatetime;
	@Column(name = "support_remarks")
	private String supportRemarks;
	
	@Column(name = "da_email")
	private String daEmail;
	@Column(name = "da_mobile")
	private String daMobile;
	@Column(name = "da_name")
	private String daName;
	@Column(name = "da_ip")
	private String daIp;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	@Column(name = "da_datetime")
	private LocalDateTime daDatetime;
	@Column(name = "da_remarks")
	private String daRemarks;
	
	@Column(name = "admin_email")
	private String adminEmail;
	@Column(name = "admin_mobile")
	private String adminMobile;
	@Column(name = "admin_name")
	private String adminName;
	@Column(name = "admin_ip")
	private String adminIp;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	@Column(name = "admin_datetime")
	private LocalDateTime adminDatetime;
	@Column(name = "admin_remarks")
	private String adminRemarks;
	
	@Column(name = "ca_sign_cert")
	private String caSignCert;
	
	@Column(name = "ca_rename_sign_cert")
	private String caRenameSignCert;
	
	@Column(name = "app_user_type")
	private String 	appUserType;
	@Column(name = "app_ca_type")	
	private String appCaType;
	@Column(name = "sign_cert")	
	private String signCert;
	@Column(name = "rename_sign_cert")	
	private String renameSignCert;
	
	@Column(name = "app_user_path")
	private String appUserPath;
	
	@Column(name = "app_ca_path")
	private String appCaPath;
	
	@Column(name = "on_hold")
	private String onHold;
	
	@Column(name = "hold_remarks")
	private String holdRemarks;
	
}
