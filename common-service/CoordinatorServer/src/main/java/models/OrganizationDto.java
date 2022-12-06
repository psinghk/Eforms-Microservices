package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDto {
	private String email;
	private String employment;
	private String ministry;
	private String department;
	private String otherDept;
	private String state;
	private String organization;
	}
