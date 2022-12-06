package in.nic.ashwini.eForms.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import in.nic.ashwini.eForms.config.ConfigProperties;
import models.MobileAndName;

@Service
public class UtilityService {
	
	@LoadBalanced
	private final RestTemplate restTemplate;
	
	private final ConfigProperties configProperties;
	
	@Autowired
	public UtilityService(RestTemplate restTemplate, ConfigProperties configProperties) {
		super();
		this.restTemplate = restTemplate;
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
		    protected boolean hasError(HttpStatus statusCode) {
		        return false;
		    }});
		this.configProperties = configProperties;
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
		String urlToAuthenticate = configProperties.getLdapUrl()+"/fetchAliasesAlongWithPrimary";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mail", mail);
		HttpEntity<Set> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Set.class);
		return response.getBody();
	}
	
	public MobileAndName fetchMobileAndName(String mail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Set<String>> entity = new HttpEntity<Set<String>>(headers);
		String urlToAuthenticate = configProperties.getLdapUrl()+"/fetchMobileAndName";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mail", mail);
		HttpEntity<MobileAndName> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, MobileAndName.class);
		return response.getBody();
	}
}
