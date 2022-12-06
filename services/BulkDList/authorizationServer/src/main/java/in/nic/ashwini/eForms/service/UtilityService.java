package in.nic.ashwini.eForms.service;

import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class UtilityService {
	@Value("${ldap.url}")
	private String url;

	@Value("${profile.url}")
	private String PROFILE_URL;

	@Value("${admin.url}")
	private String ADMIN_URL;

	@Value("${coord.url}")
	private String COORD_URL;

	@Value("${reporting.url}")
	private String REPORTING_URL;

	@LoadBalanced
	private final RestTemplate restTemplate;
	
	@Autowired
	public UtilityService(RestTemplate restTemplate) {
		super();
		this.restTemplate = restTemplate;
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
		    protected boolean hasError(HttpStatus statusCode) {
		        return false;
		    }});
	}

	public boolean isSupportEmail(String email) {
		if (email.equals("support@gov.in") || email.equals("support@nic.in") || email.equals("support@dummy.nic.in")
				|| email.equals("support@nkn.in") || email.equals("vpnsupport@nic.in")
				|| email.equals("vpnsupport@gov.in") || email.equals("smssupport@gov.in")
				|| email.equals("smssupport@nic.in")) {
			return true;
		}
		return false;
	}

	public boolean isUserRegistered(String email) {
		String uri = PROFILE_URL + "/isUserRegistered?email=" + email;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public boolean isMobileRegisteredInEforms(String mobile) {
		String uri = PROFILE_URL + "/isMobileRegisteredInEforms?mobile=" + mobile;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public String fetchMobileFromProfile(String email) {
		String uri = PROFILE_URL + "/fetchMobileFromProfile?email=" + email;
		return restTemplate.getForObject(uri, String.class);
	}

	public boolean isUserRo(String email) {
		String uri = PROFILE_URL + "/isUserRo?email=" + email;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public Set<String> fetchAliasesFromLdap(String mail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Set<String>> entity = new HttpEntity<Set<String>>(headers);
		String urlToAuthenticate = url + "/fetchAliasesAlongWithPrimary";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mail", mail);
		HttpEntity<Set> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, Set.class);
		return response.getBody();
	}

	public String transformMobile(String mobile) {
		if (!mobile.contains("+")) {
			if (mobile.length() == 10) {
				mobile = "+91" + mobile;
			} else if (mobile.length() == 12 && mobile.startsWith("91")) {
				mobile = "+" + mobile;
			} else {
				throw new BadCredentialsException("Invalid Mobile Number!!!");
			}
		}
		return mobile;
	}

	public Boolean isMobileAvailableInLdap(String mobile) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlToAuthenticate = url + "/isMobileAvailable";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mobile", mobile);
		HttpEntity<Boolean> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity,
				Boolean.class);
		return response.getBody();
	}

	public Boolean isGovEmployee(String mail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlToAuthenticate = url + "/isEmailAvailable";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mail", mail);
		HttpEntity<Boolean> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity,
				Boolean.class);
		return response.getBody();
	}

	public Boolean isNicEmployee(String mail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlToAuthenticate = url + "/isNicEmployee";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mail", mail);
		HttpEntity<Boolean> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity,
				Boolean.class);
		return response.getBody();
	}

	public String fetchMobile(String mail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlToAuthenticate = url + "/fetchMobile";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mail", mail);
		HttpEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity,
				String.class);
		return response.getBody();
	}

	public Boolean authenticateThroughLdap(String mail, String password) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlToAuthenticate = url + "/authenticate";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("username", mail)
				.queryParam("password", password);
		HttpEntity<Boolean> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity,
				Boolean.class);
		return response.getBody();
	}

	public String maskString(String strText, int start, int end, char maskChar) throws Exception {

		if (strText == null || strText.equals(""))
			return "";

		if (start < 0)
			start = 0;

		if (end > strText.length())
			end = strText.length();

		if (start > end)
			throw new Exception("End index cannot be greater than start index");

		int maskLength = end - start;

		if (maskLength == 0)
			return strText;

		StringBuilder sbMaskString = new StringBuilder(maskLength);

		for (int i = 0; i < maskLength; i++) {
			sbMaskString.append(maskChar);
		}

		return strText.substring(0, start) + sbMaskString.toString() + strText.substring(start + maskLength);
	}

	public String fetchClientIp(HttpServletRequest request) {
		String remoteAddr = "";
		if (request != null) {
			remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			} else {
				remoteAddr = new StringTokenizer(remoteAddr, ",").nextToken().trim();
			}
		}
		return remoteAddr;
	}

	public boolean isUserRoForSettingRole(String email) {
		String uri = PROFILE_URL + "/isUserRo?email=" + email;
		Boolean isRoFromProfile = restTemplate.getForObject(uri, Boolean.class);
		Boolean isRoFromStatusTable = false;
		if (!isRoFromProfile) {
			uri = REPORTING_URL + "/isUserRo?email=" + email;
			isRoFromStatusTable = restTemplate.getForObject(uri, Boolean.class);
		}
		return (isRoFromProfile || isRoFromStatusTable);
	}

	public boolean isUserCo(String ip, String email) {
		String uri = COORD_URL + "/isUserCo?ip=" + ip + "&email=" + email;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public boolean isUserVpnCo(String email) {
		String uri = COORD_URL + "/isUserVpnCo?email=" + email;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public boolean isUserAdmin(String remoteIp, String email, String mobile) {
		String uri = ADMIN_URL + "/isUserAdmin?remoteIp=" + remoteIp + "&email=" + email + "&mobile=" + mobile;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public boolean isUserSupport(String remoteIp, String email, String mobile) {
		String uri = ADMIN_URL + "/isUserSupport?remoteIp=" + remoteIp + "&email=" + email + "&mobile=" + mobile;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public boolean isUserDashboardAdmin(String email) {

		String uri = ADMIN_URL + "/isUserDashboardAdmin?email=" + email;
		return restTemplate.getForObject(uri, Boolean.class);
	}
}
