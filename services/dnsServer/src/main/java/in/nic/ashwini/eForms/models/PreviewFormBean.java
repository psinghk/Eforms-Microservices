package in.nic.ashwini.eForms.models;

import javax.validation.constraints.NotEmpty;

import com.sun.istack.NotNull;

import lombok.Data;

@Data
public class PreviewFormBean {
	@NotEmpty
	private String empCode;

	private String organization;
	@NotEmpty
	private String employment;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;
	private String remarks;
	@NotNull
	private Boolean tnc;
}
