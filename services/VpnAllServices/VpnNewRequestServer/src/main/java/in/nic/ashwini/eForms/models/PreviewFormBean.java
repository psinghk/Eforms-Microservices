package in.nic.ashwini.eForms.models;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;

@Data
public class PreviewFormBean {

	@Valid
	private List<FormData> vpnFormDetails;

	private String organization;

	private String employment;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;
	private String remarks;
	private String req_for;

	private Boolean tnc;

}
