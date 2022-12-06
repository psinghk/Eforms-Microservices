package in.nic.ashwini.eForms.models;

import java.util.List;

import lombok.Data;

@Data
public class RequestBean {
	private List<String> applicantEmails;
	private List<String> status;
	private List<String> aliases;
}
