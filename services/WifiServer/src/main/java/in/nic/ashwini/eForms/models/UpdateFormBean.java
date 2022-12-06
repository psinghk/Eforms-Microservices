package in.nic.ashwini.eForms.models;

import lombok.Data;

@Data
public class UpdateFormBean {
	private String organization;
	// @NotEmpty
	private String employment;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;
	private String remarks;
	private String wifiRequest;
	// @NotNull
	private Boolean tnc;
}
