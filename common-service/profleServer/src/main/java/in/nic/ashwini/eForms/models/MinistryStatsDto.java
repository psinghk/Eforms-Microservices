package in.nic.ashwini.eForms.models;

import java.util.List;

import lombok.Data;

@Data
public class MinistryStatsDto {
	private String employment;
	private String ministry;
	private String department;
	private List<String> applicants;
	private Integer totalApplicants;
	private Integer totalRequests;
}
