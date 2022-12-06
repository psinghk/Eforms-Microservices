package in.nic.ashwini.eForms.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class FormData {
	String id;
	@Pattern(regexp ="^(([0-9A-Fa-f]{2}[:]){5}([0-9A-Fa-f]{2}))|(([0-9A-Fa-f]{2}[\\-]){5}([0-9A-Fa-f]{2}))$", message = "Please enter correct machine address")
	@NotBlank(message = "machine address should not be empty")
	String machineAddress;
	@Pattern(regexp ="-?\\d+(\\.\\d+)?", message = "please enter operating system in correct format")
	@NotBlank(message = "operating system should not be empty")
	String operatingSystem;
	@NotBlank(message = "device type should not be empty")
	String deviceType;
}
