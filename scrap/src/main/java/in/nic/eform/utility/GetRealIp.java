package in.nic.eform.utility;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;


@Component
public class GetRealIp {

	public String getRealIp(HttpServletRequest request){
		
		String forwards = request.getHeader("X-Forwarded-For");
		if(StringUtils.isNotBlank(forwards)){
			System.out.println("Entering forwards");
			 String ip = StringUtils.substringBefore(forwards, ",");
			 return ip;
		}
		 else if (StringUtils.isNotBlank(request.getRemoteAddr())) {
			 System.out.println("Entering remote address");
			 return request.getRemoteAddr();
		 }
		return "";
			
	}
	
}
