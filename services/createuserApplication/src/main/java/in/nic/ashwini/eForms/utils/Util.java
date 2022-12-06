package in.nic.ashwini.eForms.utils;

import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import in.nic.ashwini.eForms.dto.AvalableSpace;
import in.nic.ashwini.eForms.dto.UserBean;
import in.nic.ashwini.eForms.dto.UserForCreate;

@Component
public class Util {
	@Value("${ldap.url}")
	private String LDAP_URL;
	
	@LoadBalanced
	private final RestTemplate restTemplate;

	
	@Autowired
	public Util(RestTemplate restTemplate) {
		super();
		this.restTemplate = restTemplate;
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			protected boolean hasError(HttpStatus statusCode) {
				return false;
			}
		});
	}
	
	public Boolean isGovEmployee(String mail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlToAuthenticate = LDAP_URL + "/isEmailAvailable";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mail", mail);
		HttpEntity<Boolean> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity,
				Boolean.class);
		return response.getBody();
	}

	public List<Object> fetchPO(String o) {
		String uri = LDAP_URL + "/fetchPos?base=" + o;
		ResponseEntity<List<Object>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Object>>() {
				});
		return response.getBody();
	}
	
	public List<Object> fetchBos(String dn) {
		String uri = LDAP_URL + "/fetchBos?base=" + dn;
		ResponseEntity<List<Object>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Object>>() {
				});
		return response.getBody();
	}
	

	
	//for mail box
	public Boolean createMailUsers(UserForCreate user, String po, String bo) {
		final String uri = LDAP_URL + "/createMailUsers?bo=" + bo + "&parentBo=" + po;
		ResponseEntity<Boolean> temp = restTemplate.postForEntity(uri, user, Boolean.class);
		return temp.getBody();
	}
	
	//for without mail box 
	public Boolean createAppUsers(UserForCreate user, String po, String bo) {
		final String uri = LDAP_URL + "/createAppUsers?bo=" + bo + "&parentBo=" + po;
		ResponseEntity<Boolean> temp = restTemplate.postForEntity(uri, user, Boolean.class);
		return temp.getBody();
	}
	
	
	public List<AvalableSpace> fetchCount(String bo) {
		String uri = LDAP_URL + "/fetchServicePackage?bo=" +bo;
		ResponseEntity<List<AvalableSpace>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<AvalableSpace>>() {
				});
		return response.getBody();
	}
	
	public UserBean initializebean(CSVRecord csvRecord, UserBean userbean) {

		userbean.setUsername(csvRecord.get(0));
		userbean.setFirstName(csvRecord.get(1));
		userbean.setLastName(csvRecord.get(2));
		userbean.setPassword(csvRecord.get(3));
		userbean.setAccount_type(csvRecord.get(4));
		userbean.setMobile(csvRecord.get(5));
		userbean.setDob(csvRecord.get(6));
		userbean.setDor(csvRecord.get(7));
		userbean.setDesignation(csvRecord.get(8));
		userbean.setDepartment(csvRecord.get(9));
		userbean.setState(csvRecord.get(10));
		userbean.setEmpcode(csvRecord.get(11));
		userbean.setEmail(csvRecord.get(12));
		return userbean;

	}
	
	
}
