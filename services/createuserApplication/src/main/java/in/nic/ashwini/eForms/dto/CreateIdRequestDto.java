package in.nic.ashwini.eForms.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class CreateIdRequestDto {

	@NotEmpty
	private String bo;

	@NotEmpty
	private String po;
	
	@NotEmpty
	private String request_for;
	

	@NotEmpty
	private String remarks;
	
	
	
}
