package in.nic.ashwini.eForms.models;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class TrackDto {
	private List<String> roles;
	private List<String> troles;
	private String status;
	private String applicantName;
	private String applicantEmail;
	private String applicantMobile;
	private String formName;
	private String regNumber;
	private String message;
	private LocalDateTime submissionDateTime;
}
