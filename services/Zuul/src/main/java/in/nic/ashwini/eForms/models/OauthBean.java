package in.nic.ashwini.eForms.models;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class OauthBean {
	@Email
	@NotBlank(message = "Email can not be blank")
	@Size(min=8)
	private String username;
	private String password;
	@NotBlank(message = "Application Name can not be blank")
	@Pattern(regexp="^[A-Za-z0-9]+[_)(-]*$",message = "Please check application name. It seems invalid.")
	private String appName;
}
