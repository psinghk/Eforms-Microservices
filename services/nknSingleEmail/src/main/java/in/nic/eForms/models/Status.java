package in.nic.eForms.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Data;

@Data
public class Status implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Long id;
	private String registrationNo;
	private String status;
	private String senderType;
	private String sender;
	private String recipientType;
	private String recipient;
	private String remarks;
	private String senderEmail;
	private String senderMobile;
	private String senderName;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime senderDatetime;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime createdon;
	private String onholdStatus;
	private String 	submissionType;
	private String 	formType;
	private String 	ip;
	private String 	senderIp;
	private String finalId;
}
