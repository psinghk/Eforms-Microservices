package in.nic.ashwini.eForms.models;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Valid
public class PreviewFormBean {

	@NotNull
	private String vpnRegNo;

	private String organization;
	private String employment;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;

	
	private String remarks;
	
}
