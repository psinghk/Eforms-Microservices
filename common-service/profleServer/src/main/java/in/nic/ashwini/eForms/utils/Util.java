package in.nic.ashwini.eForms.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import in.nic.ashwini.eForms.config.ConfigProperties;
import in.nic.ashwini.eForms.models.HodDetailsDto;

@Service
public class Util {
	
	private static final String RETIREMENT_AGE_MESSAGE = "year of retirement can not exceed 67 years from the DOB year";
	private static final String DATEOFBIRTH_MESSAGE = "minimum age is 18 years and maximum age is 67 years";
	
	@LoadBalanced
	private final RestTemplate restTemplate;
	
	private final ConfigProperties configProperties;
	
	@Autowired
	public Util(RestTemplate restTemplate, ConfigProperties configProperties) {
		super();
		this.restTemplate = restTemplate;
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
		    protected boolean hasError(HttpStatus statusCode) {
		        return false;
		    }});
		this.configProperties = configProperties;
	}

	public Boolean validateEmailForGovtEmployee(String email) {
		final String uri = configProperties.getLdapUrl() + "/validateEmail?mail=" + email;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public HodDetailsDto getHodValues(String email) {
		final String uri = configProperties.getLdapUrl() + "/findHodDetails?mail=" + email;
		return restTemplate.getForObject(uri, HodDetailsDto.class);
	}

	public Boolean isGovEmployee(String mail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlToAuthenticate = configProperties.getLdapUrl() + "/isEmailAvailable";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mail", mail);
		HttpEntity<Boolean> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
				Boolean.class);
		return response.getBody();
	}

	public Boolean isNicEmployee(String mail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlToAuthenticate = configProperties.getLdapUrl() + "/isNicEmployee";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mail", mail);
		HttpEntity<Boolean> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
				Boolean.class);
		return response.getBody();
	}

	public String allLdapValues(String email) {
		final String uri = configProperties.getLdapUrl() + "/findByUidOrMailOrEquivalent?mail=" + email;
		return restTemplate.getForObject(uri, String.class);
	}
	
//	public UserProfile allLdapValuesInBean(String email) {
//		final String uri = url + "/findByUidOrMailOrEquivalent?mail=" + email;
//		UserForSearch profileFromLdap = restTemplate.getForObject(uri, UserForSearch.class);
//	}

	public List<String> aliases(String email) {
		String uri = configProperties.getLdapUrl() + "/fetchAliasesAlongWithPrimary?mail=" + email;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}
	
	public String dorValidation(String dor, String dob) throws ParseException {
		String msg = "";
		if (dor.matches("([0-9]{2})[-][0-9]{2}[-][0-9]{4}")) {

			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

			formatter.setLenient(false);
			Calendar cal1 = Calendar.getInstance();
			Date dr = formatter.parse(dor);
			cal1.setTime(dr);
			if (!dob.isEmpty()) {
				Date date1 = formatter.parse(dob);
				cal1.setTime(date1);
				long diff = formatter.parse(dor).getTime() - formatter.parse(dob).getTime();
				long diffHours = diff / (60 * 60 * 1000);
				long days = (diffHours / 24);
				long years = (days / 365);
				if ((years) > 67 || ((years) == 67) && (cal1.get(Calendar.MONTH) > cal1.get(Calendar.MONTH))) {
					msg = RETIREMENT_AGE_MESSAGE;
				}
				Date date2 = formatter.parse(dor);
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(date2);
				int yr60 = (cal1.get(Calendar.YEAR)) + 49 + 1900;
				int yc = (cal2.get(Calendar.YEAR)) + 1900;
				int yt = (cal1.get(Calendar.YEAR)) + 1900;
				int mr1 = (cal1.get(Calendar.MONTH)) + 1;
				int mr = (cal2.get(Calendar.YEAR)) + 1;
				int d1 = (cal1.get(Calendar.DATE));
				int d2 = (cal2.get(Calendar.YEAR));
				if (yc < yt || yc > yr60) {
					msg = "Please enter Date Of retirement in correct format";
				} else if (yc == yt) {
					if (mr < mr1 || (mr == mr1 && d2 < d1))
						msg = RETIREMENT_AGE_MESSAGE;
				}
			}
		} else {
			msg = "Please enter Date Of retirement in correct format";

		}

		return msg;
	}

	public String dobValidation(String value) throws ParseException {
		String msg = "";
		if (value.matches("([0-9]{2})[-][0-9]{2}[-][0-9]{4}")) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			String pdate = formatter.format(new Date());
			Date date1 = formatter.parse(pdate);
			Date date2 = formatter.parse(value);
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(date1);
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(date2);
			msg = validateDate(cal1, cal2);
		} else {
			msg = "Please enter date of birth in correct format";

		}
		return msg;
	}

	private String validateDate(Calendar cal1, Calendar cal2) {
		String msg = "";
		int y18 = (cal1.get(Calendar.YEAR) - 18) + 1900;
		int y60 = (cal1.get(Calendar.YEAR) - 67) + 1900;
		int dcl = (cal2.get(Calendar.YEAR)) + 1900;
		boolean flag = ((cal2.get(Calendar.MONTH)) + 1) == ((cal1.get(Calendar.MONTH)) + 1);
		if (dcl > y18 || dcl < y60) {
			msg = DATEOFBIRTH_MESSAGE;
		} else if (dcl == y60) {
			if (((cal2.get(Calendar.MONTH)) + 1) < ((cal1.get(Calendar.MONTH)) + 1)) {
				msg = DATEOFBIRTH_MESSAGE;
			} else if (flag || cal2.get(Calendar.DATE) < cal1.get(Calendar.DATE)) {
				msg = DATEOFBIRTH_MESSAGE;
			} else {
				msg = "";
			}
		} else if (dcl == y18) {
			if (((cal2.get(Calendar.MONTH)) + 1) > ((cal1.get(Calendar.MONTH)) + 1)) {
				msg = DATEOFBIRTH_MESSAGE;
			} else if (flag) {
				if (cal2.get(Calendar.DATE) > cal1.get(Calendar.DATE)) {
					msg = DATEOFBIRTH_MESSAGE;
				} else {
					msg = "";
				}
			} else {
				msg = "";
			}
		}
		return msg;
	}
}
