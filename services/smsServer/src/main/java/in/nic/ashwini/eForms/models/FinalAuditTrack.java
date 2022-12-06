package in.nic.ashwini.eForms.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Data;

@Data
public class FinalAuditTrack implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String registrationNo;
	private String applicantEmail;
	private String applicantMobile;
	private String applicantName;
	private String applicantIp;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime applicantDatetime;
	private String applicantRemarks;

	private String toEmail;
	private String toName;
	private String toMobile;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime toDatetime;
	private String status;
	private String formName;
	
	private String caEmail;
	private String caMobile;
	private String caName;
	private String caIp;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime caDatetime;
	private String caRemarks;
	
	private String usEmail;
	private String usMobile;
	private String usName;
	private String usIp;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime usDatetime;
	private String usRemarks;
	
	private String coordinatorEmail;
	private String coordinatorMobile;
	private String coordinatorName;
	private String coordinatorIp;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime coordinatorDatetime;
	private String coordinatorRemarks;
	
	private String supportEmail;
	private String supportMobile;
	private String supportName;
	private String supportIp;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime supportDatetime;
	private String supportRemarks;
	
	private String daEmail;
	private String daMobile;
	private String daName;
	private String daIp;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime daDatetime;
	private String daRemarks;
	
	private String adminEmail;
	private String adminMobile;
	private String adminName;
	private String adminIp;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime adminDatetime;
	private String adminRemarks;
	
	private String caSignCert;
	
	private String caRenameSignCert;
	
	private String 	appUserType;
	private String appCaType;
	private String signCert;
	private String renameSignCert;
	
	private String appUserPath;
	
	private String appCaPath;
	
	private String onHold;
	
	private String holdRemarks;
	
	
}
