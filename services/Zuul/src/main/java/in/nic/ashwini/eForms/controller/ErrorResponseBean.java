package in.nic.ashwini.eForms.controller;

import lombok.Data;

@Data
public class ErrorResponseBean {
	private Integer errorCode;
	private String reason;
}
