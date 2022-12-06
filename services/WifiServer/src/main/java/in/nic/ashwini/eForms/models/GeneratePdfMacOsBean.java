package in.nic.ashwini.eForms.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GeneratePdfMacOsBean {
	
	private String applicantName;
	private String applicantEmail;
	private String description;
	private String applicantMobile;
	private String hodName;
	private String hodEmail;
	private String hodMobile;
	@JsonProperty("wifiFormDetails")
	private List<FormData> wifiFormDetails = new ArrayList<>();
	private String ministry;
	
}
