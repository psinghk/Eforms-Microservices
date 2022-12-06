package in.nic.ashwini.eForms.db.slave.entities;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class OrganizationBeanFromSlave {
	@NotEmpty
	private String employment;
	private String organization;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;
	private String postingState;
	private String city;
}
