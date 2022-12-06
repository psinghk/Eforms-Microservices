package in.nic.ashwini.eForms.models;

import java.util.ArrayList;

import javax.validation.Valid;

import lombok.Data;

@Data
public class PreviewFormBean {

	@Valid
	private ArrayList<FormData> vpnFormDetails = new ArrayList<>();
	
	private String vpnRegNo;

	private String organization;
	// @NotEmpty
	private String employment;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;
	private String remarks;
	// @NotNull
	private Boolean tnc;
	
	private String  req_for;
	
}
