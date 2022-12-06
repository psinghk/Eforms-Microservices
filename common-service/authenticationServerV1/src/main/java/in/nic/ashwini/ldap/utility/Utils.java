package in.nic.ashwini.ldap.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public class Utils {

	public String genericDateFormater(String dateString) {

		if (dateString == null)
			return null;
		if (dateString.isEmpty())
			return "";

		if (dateString.endsWith("z")) {
			dateString = dateString.replaceAll("z", "");
		} else if (dateString.endsWith("Z")) {
			dateString = dateString.replaceAll("Z", "");
		}

		String dateFormated = dateString;

		List<String> dateFormaters = new ArrayList<String>();
		dateFormaters.add("yyyyMMddHHmmss");
		dateFormaters.add("yyyyMMdd");
		dateFormaters.add("yyyy-MM-dd");
		dateFormaters.add("dd-MM-yy");
		dateFormaters.add("MM-dd-yyyy");
		dateFormaters.add("dd-MM-yyyy");

		DateFormat destDf = new SimpleDateFormat("yyyy-MM-dd");
		for (String format : dateFormaters) {

			boolean isValid = isValidFormat(format, dateString);
			if (isValid) {

				DateFormat srcDf = new SimpleDateFormat(format);
				Date date;
				try {
					date = srcDf.parse(dateString);
					dateFormated = destDf.format(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				break;
			}
		}

		return dateFormated;
	}

	private boolean isValidFormat(String format, String value) {

		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(value);
			if (!value.equals(sdf.format(date))) {
				date = null;
			}
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return date != null;
	}
	
	public String fetchClientIp(HttpServletRequest request) {
		String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }else {
            	remoteAddr = new StringTokenizer(remoteAddr, ",").nextToken().trim();
            }
        }
		return remoteAddr;
	}
}