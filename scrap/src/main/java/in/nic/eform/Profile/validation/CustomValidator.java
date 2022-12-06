package in.nic.eform.Profile.validation;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import in.nic.eform.Profile.model.mapper.ProfileDto;
import in.nic.eform.Profile.utils.Util;

public class CustomValidator implements ConstraintValidator<CustomValidation, ProfileDto> {
	
	// shift these variables to either property file or constant class
	
	public static final String UNDER_SEC_EMAIL = "underSecEmail";
	public static final String UNDER_SEC_MOBILE = "underSecMobile";
	public static final String UNDER_SEC_TELEPHONE = "underSecTelephone";
	public static final String UNDER_SEC_DESIGNATION = "underSecDesig";
	public static final String EMAIL_REGEX = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$";
	public static final String MINISTRY_DEPARTMENT_ORGANIZATION_REGEX = "^[a-zA-Z#0-9\\s,'.\\-\\/\\&\\_(\\)]{2,100}$";
	public static final String MINISTRY = "ministry";
	public static final String DEPARTMENT = "department";
	public static final String STATE = "state";
	public static final String ORGANIZATION = "organization";
	public static final String OTHER_DEPT = "other_dept";

	@Override
	public void initialize(CustomValidation constraintAnnotation) {
	}

	public boolean isValid(ProfileDto object, ConstraintValidatorContext context) {
		if (!(object instanceof ProfileDto)) {
			throw new IllegalArgumentException("@Profile only applies to ProfileDto");
		}
		ProfileDto profileDto = object;
		boolean isValid = true;
		List<String> errMsg = new ArrayList<String>();
		List<String> errNode = new ArrayList<String>();

		try {
			
			Boolean isGovtEmp = true;
			if (profileDto.getHodEmail().matches(EMAIL_REGEX)) {
				isGovtEmp = Util.validateEmailForGovtEmployee(profileDto.getHodEmail());
			}
			if (!isGovtEmp.booleanValue()) {
				if (!profileDto.getUnderSecEmail().isEmpty() && !profileDto.getUnderSecEmail().matches(EMAIL_REGEX)) {
					errMsg.add("Under secretary email is not in correct format");
					errNode.add(UNDER_SEC_EMAIL);
					isValid = false;

				} else {
					errNode.add(UNDER_SEC_EMAIL);
					errMsg.add("Under secretary email should not be empty");
					isValid = false;
				}

				if (!profileDto.getUnderSecMobile().isEmpty()
						&& !profileDto.getUnderSecMobile().matches("^[+0-9]{13}$")) {
					errMsg.add("Under secretary mobile is not in correct format");
					errNode.add(UNDER_SEC_MOBILE);
					isValid = false;

				} else {
					errMsg.add("Under secretary mobile should not be empty");
					errNode.add(UNDER_SEC_MOBILE);
					isValid = false;
				}
				if (!profileDto.getUnderSecDesig().isEmpty()) {
					if (!profileDto.getUnderSecDesig().matches("-?\\d+(\\.\\d+)?")) {
						errMsg.add("Under secretary designation is not in correct format");
						errNode.add(UNDER_SEC_DESIGNATION);
						isValid = false;
					}
				} else {
					errMsg.add("Under secretary designation should not be empty");
					errNode.add(UNDER_SEC_DESIGNATION);
					isValid = false;
				}

				if (!profileDto.getUnderSecTelephone().isEmpty()) {
					if (!profileDto.getUnderSecTelephone().matches("^[+0-9]{3,5}[-]([0-9]{6,15})$")) {
						errMsg.add("Under secretary telephone is not in correct format");
						errNode.add(UNDER_SEC_TELEPHONE);
						isValid = false;
					}
				} else {
					errMsg.add("Under secretary designation should not be empty");
					errNode.add(UNDER_SEC_TELEPHONE);
					isValid = false;
				}
			}
			if (profileDto.getEmployment().equalsIgnoreCase("central")) {
				if (!profileDto.getMinistry().isEmpty()) {
					if (!profileDto.getMinistry().matches(MINISTRY_DEPARTMENT_ORGANIZATION_REGEX)) {
						errMsg.add("Please enter ministry in correct format");
						errNode.add(MINISTRY);
						isValid = false;
					}
				} else {
					errMsg.add("Ministry should not be empty");
					errNode.add(MINISTRY);
					isValid = false;
				}

				if (!profileDto.getDepartment().isEmpty()) {
					if (!profileDto.getDepartment().matches(MINISTRY_DEPARTMENT_ORGANIZATION_REGEX)) {
						errMsg.add("Please enter department in correct format");
						errNode.add(DEPARTMENT);
						isValid = false;
					}
				} else {
					errMsg.add("Department should not be empty");
					errNode.add(DEPARTMENT);
					isValid = false;
				}

			}

			if (profileDto.getEmployment().equalsIgnoreCase(STATE)) {
				if (!profileDto.getState().isEmpty()) {
					if (!profileDto.getState().matches(MINISTRY_DEPARTMENT_ORGANIZATION_REGEX)) {
						errMsg.add("Please enter state in correct format");
						errNode.add(STATE);
						isValid = false;
					}
				} else {
					errMsg.add("State should not be empty");
					errNode.add(STATE);
					isValid = false;
				}

				if (!profileDto.getDepartment().isEmpty()) {
					if (!profileDto.getDepartment().matches(MINISTRY_DEPARTMENT_ORGANIZATION_REGEX)) {
						errMsg.add("Please enter department in correct format");
						errNode.add(DEPARTMENT);
						isValid = false;
					}
				} else {
					errMsg.add("Department should not be empty");
					errNode.add(DEPARTMENT);
					isValid = false;
				}

			}

			if (profileDto.getEmployment().equalsIgnoreCase("psu") || profileDto.getEmployment().equalsIgnoreCase("nkn")
					|| profileDto.getEmployment().equalsIgnoreCase("others")) {
				if (!profileDto.getOrganization().isEmpty()) {
					if (!profileDto.getOrganization().matches(MINISTRY_DEPARTMENT_ORGANIZATION_REGEX)) {
						errMsg.add("Please enter organization in correct format");
						errNode.add(ORGANIZATION);
						isValid = false;
					}
				} else {
					errMsg.add("Organization should not be empty");
					errNode.add(ORGANIZATION);
					isValid = false;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!errMsg.isEmpty() && !errNode.isEmpty()) {
			context.disableDefaultConstraintViolation();
			for (int i = 0; i < errMsg.size(); i++)
				context.buildConstraintViolationWithTemplate(errMsg.get(i)).addPropertyNode(errNode.get(i))
						.addConstraintViolation();
		}

		return isValid;
	}
}