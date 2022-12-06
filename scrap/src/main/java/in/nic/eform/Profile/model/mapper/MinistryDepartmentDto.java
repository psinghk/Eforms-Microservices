package in.nic.eform.Profile.model.mapper;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class MinistryDepartmentDto {
	@NotNull(message = "First name is mandatory field")
	private String employment;
	private String ministry;
	private String department;

}
