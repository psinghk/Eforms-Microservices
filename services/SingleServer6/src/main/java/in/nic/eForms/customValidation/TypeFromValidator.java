package in.nic.eForms.customValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.nic.eForms.models.PreviewFormBean;

public class TypeFromValidator implements ConstraintValidator<TypeFrom, PreviewFormBean> {

	String appname = "", requestType = "", ipRangFrom = "";

	@Override
	public boolean isValid(PreviewFormBean previewFormBean, ConstraintValidatorContext context) {

		String requestType = previewFormBean.getReqUserType();
		String appname = previewFormBean.getApplicantName();
		if (requestType.equalsIgnoreCase("other")) {
			if (appname == null)
				return false;
			if (appname.isEmpty())
				return false;
			if (!appname.matches("^[a-zA-Z .,]{1,50}$"))

				return true;

			else {
				return false;
			}
		}
		return true;
	}

}