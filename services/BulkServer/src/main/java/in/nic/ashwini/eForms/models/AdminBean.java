package in.nic.ashwini.eForms.models;

import lombok.Data;

@Data
public class AdminBean {
	private String employmentType;
	
	private String description;//free/paid
	private String po;
	private String bo;
	private String domain;
			
	private String statRemarks; 	//remarks;
	

}
