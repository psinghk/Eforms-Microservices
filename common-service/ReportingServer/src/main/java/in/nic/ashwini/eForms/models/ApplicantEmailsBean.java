package in.nic.ashwini.eForms.models;

import java.util.List;

import lombok.Data;

@Data
public class ApplicantEmailsBean {
	private List<String> applicantEmails;
	private List<String> coordinatorEmails;
	private String coordEmail;
}
