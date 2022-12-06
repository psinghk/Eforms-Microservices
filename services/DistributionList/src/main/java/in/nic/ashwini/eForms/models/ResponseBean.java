package in.nic.ashwini.eForms.models;

import java.util.Map;

import lombok.Data;

@Data
public class ResponseBean {
	private String regNumber;
	private String requestType;
	private String status;
	private Map<String,Object> errors;
}
