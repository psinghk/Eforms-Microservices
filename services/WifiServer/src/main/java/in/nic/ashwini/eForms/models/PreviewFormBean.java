package in.nic.ashwini.eForms.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PreviewFormBean {
	@JsonProperty("wifiFormDetails")
	private List<FormData> wifiFormDetails = new ArrayList<>();
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
