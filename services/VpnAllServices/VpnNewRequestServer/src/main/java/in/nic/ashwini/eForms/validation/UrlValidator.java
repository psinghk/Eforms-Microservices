package in.nic.ashwini.eForms.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UrlValidator implements ConstraintValidator<Url, String> {

	@Override
	public boolean isValid(String url, ConstraintValidatorContext context) {

		boolean flag = false;
		if (!url.isEmpty()) {
			if (url.matches(
					"^(?:(?:(?:https?|ftp):)?\\/\\/)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})).?)(?::\\d{2,5})?(?:[/?#]\\S*)?$")) {
				flag = true;
			} else if (url.matches(
					"^((http:\\/\\/|https:\\/\\/)([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\/[a-z]{1,})*$")) {
				String ip = url.substring(url.indexOf("//") + 2, url.length());
				if (ip.startsWith("0") || ip.equals("0.0.0.0") || ip.equals("127.0.0.1") || ip.equals("255.255.255.255")
						|| ip.endsWith("255")) {
					flag = true;
				} 
			} 
		}else {
			//if it is empty
			flag = true;
		}
		return flag;

	}

}
