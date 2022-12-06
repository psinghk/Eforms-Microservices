package models;

import lombok.Data;

@Data
public class ErrorResponseForOrganizationValidationDto {
	private String employmentCategoryError;
	private String ministryError;
	private String departmentError;
	private String otherDeptError;
	private String stateError;
	private String organizationError;
}
