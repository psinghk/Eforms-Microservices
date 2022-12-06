package in.nic.ashwini.eForms.services;

import java.util.Set;

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
	
	public Set<String> fetchAliasesFromLdap(String mail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Set<String>> entity = new HttpEntity<Set<String>>(headers);
		String urlToAuthenticate = url+"/fetchAliasesAlongWithPrimary";
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
}
