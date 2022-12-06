package in.nic.ashwini.eForms.models;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class OauthOtpBean {
	@NotBlank(message = "Token can not be blank")
	private String token;
	@NotBlank(message = "OTP can not be blank")
	private String otp;
	@NotBlank(message = "Application name can not be blank")
	private String service;
}
