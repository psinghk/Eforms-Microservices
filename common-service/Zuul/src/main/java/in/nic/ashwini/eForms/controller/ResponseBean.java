package in.nic.ashwini.eForms.controller;

import lombok.Data;

@Data
public class ResponseBean {
	private Boolean govEmployee = false;
	private Boolean registeredUser = false;
	private String email;
}
