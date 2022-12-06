package in.nic.ashwini.eForms.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.config.ConfigProperties;
import in.nic.ashwini.eForms.models.Person;

@Service
public class UtilityService {

	@Autowired
	@LoadBalanced
	private RestTemplate restTemplate;
	
	@Autowired
	private ConfigProperties configProperties;

	public List<String> aliases(String email) {
		String uri = configProperties.getAdminUrl() + "/fetchAliasesAlongWithPrimary?mail=" + email;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	public Boolean isRegNumberMatchesWithApiCall(String regNumber) {
		final String uri = configProperties.getAdminUrl() + "/isRegNumberMatchesWithApiCall?regNumber=" + regNumber;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public Boolean isRequestPendingWithLoggedInUser(String regNumber, String email) {
		final String uri = configProperties.getReportingUrl() + "/isRequestPendingWithLoggedInUser?regNumber=" + regNumber + "&email="
				+ email;
		return restTemplate.getForObject(uri, Boolean.class);
	}

//	public Boolean isRequestPendingWithLoggedInUserAndNextLevelAuthority(String regNumber, String email, String role) {
//		final String uri = REPORTING_URL + "/isRequestPendingWithLoggedInUserAndNextLevelAuthority?regNumber="
//				+ regNumber + "&email=" + email + "&role=" + role;
//		return restTemplate.getForObject(uri, Boolean.class);
//	}

	public Boolean isRequestPendingWithLoggedInUserOrNextLevelAuthority(String regNumber, String email, String role) {
		final String uri = configProperties.getReportingUrl() + "/isRequestPendingWithLoggedInUserOrNextLevelAuthority?regNumber="
				+ regNumber + "&email=" + email + "&role=" + email;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public Boolean isLoggedInUserStakeHolder(String regNumber, String email) {
		final String uri = configProperties.getReportingUrl() + "/isLoggedInUserStakeHolder?regNumber=" + regNumber + "&email=" + email;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public Boolean isEditable(String regNumber, String email, String role) {
		final String uri = configProperties.getReportingUrl() + "/isEditable?regNumber=" + regNumber + "&email=" + email + "&role=" + role;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public String fetchServiceName(String regNumber) {
		final String uri = configProperties.getAdminUrl() + "/fetchServiceName?regNumber=" + regNumber;
		return restTemplate.getForObject(uri, String.class);
	}

	public boolean isUserRegistered(String email) {
		String uri = configProperties.getProfileUrl() + "/isUserRegistered?email=" + email;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public Boolean isGovEmployee(String mail) {
		String uri = configProperties.getLdapUrl() + "/isEmailAvailable?mail=" + mail;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public Map<String, Object> fetchService() {
		String uri = configProperties.getAdminUrl() + "/fetchServices";
		return restTemplate.getForObject(uri, Map.class);
	}

	public Boolean isRequestPendingWithSupport(String regNumber) {
		final String uri = configProperties.getReportingUrl() + "/isRequestPendingWithSupport?regNumber=" + regNumber;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public Boolean isRequestPendingWithAdmin(String regNumber) {
		final String uri = configProperties.getReportingUrl() + "/isRequestPendingWithAdmin?regNumber=" + regNumber;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public boolean isRequestEsigned(String registrationNo, String email) {
		final String uri = configProperties.getReportingUrl() + "/isRequestEsigned?regNumber=" + registrationNo;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public String isSessionValid(String browserId, String sessionId, String localTokenId, String userName, String serviceName) {
		serviceName="coordinatorportal";
		
		String uri = "http://10.122.34.117:8081/Accounts/openam/login/isTokenValid?localTokenId="+localTokenId+"&userName="+userName+"&service="+serviceName+"&browserId="+browserId+"&sessionId="+sessionId;
		String output = HTTP_URL_Response(uri);
		ObjectMapper mapper = new ObjectMapper();
		Person person;
		try {
			person = mapper.readValue(output, Person.class);
			return person.getTokenValid();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public OAuth2AccessToken generateTokenForSSOUsers(String ua, String browserId, String sessionId, String localTokenId,
			String userName, String mobile, String ip) {
		String uri = configProperties.getOauthUrl() + "/oauth/token?grant_type=generate&username=" + userName
				+ "&service=eforms&browserId=" + browserId + "&sessionId=" + sessionId + "&localTokenId=" + localTokenId
				+ "&mobile=" + mobile + "&ip=" + ip;
//		String plainCreds = "willie:p@ssword";
//		byte[] plainCredsBytes = plainCreds.getBytes();
//		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
//		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth("eforms", "secret");
//		headers.add("Authorization", "Basic " + base64Creds);
//		HttpHeaders headers = new HttpHeaders();
//		headers.add(AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("eforms:secret".getBytes()));
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<OAuth2AccessToken> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
				OAuth2AccessToken.class);
		return response.getBody();
	}
	
	public OAuth2AccessToken authenticateUserThroughOauth(String userName, String password, String appName, String ip) {
			String uri = "http://oauth-server/oauth/token?grant_type=password&username=" + userName
					+ "&service="+appName+ "&ip=" + ip+"&password="+password;
			HttpHeaders headers = new HttpHeaders();
			headers.setBasicAuth("eforms", "secret");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<OAuth2AccessToken> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
					OAuth2AccessToken.class);
			return response.getBody();
	}

	public OAuth2AccessToken generateRefreshToken(@NotBlank String token) {
		String uri = configProperties.getOauthUrl() + "/oauth/token?grant_type=custom_refresh_token&refresh_token=" + token;
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth("eforms", "secret");
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<OAuth2AccessToken> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
				OAuth2AccessToken.class);
		return response.getBody();
	}
	
	private String HTTP_URL_Response(String http_url) {
		String line = "";

		try {

			System.out.println("SOUT " + http_url);
			HttpURLConnection huc = (HttpURLConnection) new URL(http_url).openConnection();
			// HttpURLConnection.setFollowRedirects(false);
			huc.setConnectTimeout(2 * 1000);
			huc.connect();

			try (InputStream response2 = huc.getInputStream()) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response2));
				for (String tmp; (tmp = reader.readLine()) != null;) {
					line = tmp;

				}

				reader.close();
			} catch (Exception e) {
				System.out.println("Error In HTTTP URL");
				e.printStackTrace();
			}

		} catch (Exception e) {
			System.out.println("Error In HTTTP URL");
			e.printStackTrace();
		}

		return line;
	}

	public OAuth2AccessToken validateOtp(String token, String otp, String service, String ip) {
		String uri = "http://oauth-server/oauth/token?grant_type=mfa&mfa_code=" + otp
				+ "&mfa_token="+token+"&ip="+ip+"&service="+service;
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth("eforms", "secret");
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<OAuth2AccessToken> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
				OAuth2AccessToken.class);
		return response.getBody();
	}

//	HttpHeaders createHeaders(String username, String password){
//		   return new HttpHeaders() {{
//		         String auth = username + ":" + password;
//		         byte[] encodedAuth = Base64.encodeBase64( 
//		            auth.getBytes(Charset.forName("US-ASCII")) );
//		         String authHeader = "Basic " + new String( encodedAuth );
//		         set( "Authorization", authHeader );
//		      }};
//		}

	/*
	 * public Boolean isRequestEsignedByUser(String regNumber) { String uri =
	 * IMAPPOP_URL + "/user/isRequestEsigned?regNumber="+regNumber; return
	 * restTemplate.getForObject(uri, Boolean.class); }
	 */
//	public Boolean isRequestEsignedByUser(String regNumber) {
//		String uri = SINGLE_URL + "/user/isRequestEsigned?regNumber="+regNumber;
//		return restTemplate.getForObject(uri, Boolean.class);
//	}
}
