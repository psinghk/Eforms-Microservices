package in.nic.ashwini.eForms.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.HodDetailsDto;
import in.nic.ashwini.eForms.models.MobileAndName;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.Po;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.models.UserAttributes;
import java.security.SecureRandom;

@Service
public class Util {

	private static final String RETIREMENT_AGE_MESSAGE = "year of retirement can not exceed 67 years from the DOB year";
	private static final String DATEOFBIRTH_MESSAGE = "minimum age is 18 years and maximum age is 67 years";
	public static final String SOME_THING_WENT_WRONG = "Something went worng";
	public static final String SUCCESS = "SUCCESS";
	
	@Value("${ldap.url}")
	private String LDAP_URL;
	
	@Value("${profile.url}")
	private String PROFILE_URL;
	
	@Value("${reporting.url}")
	private String REPORTING_URL;
	
	@Value("${coord.url}")
	private String COORD_URL;
	
	@Value("${organization.url}")
	private String ORGANIZATION_URL;
	
	@LoadBalanced
	private final RestTemplate restTemplate;
	
	@Autowired
	public Util(RestTemplate restTemplate) {
		super();
		this.restTemplate = restTemplate;
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
		    protected boolean hasError(HttpStatus statusCode) {
		        return false;
		    }});
	}
	
	public Boolean isNicOutsourced(String mail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlToAuthenticate = LDAP_URL+"/isNicOutsourced";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate)
				.queryParam("mail", mail);
		HttpEntity<Boolean> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity,
				Boolean.class);
		return response.getBody();
	}
	
	public List<String> fetchBoByMinistry(String empCategory,String ministry,String empDept) {
		String uri = COORD_URL + "/fetchBoByMinistry?empCategory=" + empCategory +"&ministry="+ministry+"&empDept="+empDept;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}
	
	
	public List<String> fetchBoByState(String empCategory,String state,String empDept) {
		String uri = COORD_URL + "/fetchBoByState?empCategory=" + empCategory +"&state="+state+"&empDept="+empDept;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}
	
	public List<String> fetchBoByOrg(String empCategory,String organization) {
		String uri = COORD_URL + "/fetchBoByOrg?empCategory=" + empCategory +"&organization="+organization;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
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
	
	public Boolean validateEmailForGovtEmployee(String email) {
		final String uri = LDAP_URL+"/validateEmail?mail=" + email;
		return restTemplate.getForObject(uri, Boolean.class);
	}
	
	public Boolean updateThroughMail(UserAttributes userAttributes) {
		final String uri = LDAP_URL+"/updateThroughMail?userAttributes=" + userAttributes;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public HodDetailsDto getHodValues(String email) {
		final String uri = PROFILE_URL+"/fetchRoDetailsInBean?mail=" + email;
		return restTemplate.getForObject(uri, HodDetailsDto.class);

	}
	
	public String findDn(String uid) {
		final String uri = LDAP_URL+"/findDn?uid=" + uid;
		return restTemplate.getForObject(uri, String.class);

	}
	
	public Po fetchBos(String base) {
		final String uri = LDAP_URL+"/fetchBos?base=" + base;
		return restTemplate.getForObject(uri, Po.class);

	}
	
	public String fetchGemDAForFreeAccounts() {
		final String uri = COORD_URL+"/fetchGemDAForFreeAccounts";
		return restTemplate.getForObject(uri, String.class);

	}
	
	public String fetchGemDAForPaidAccounts() {
		final String uri = COORD_URL+"/fetchGemDAForPaidAccounts";
		return restTemplate.getForObject(uri, String.class);

	}
	
	public String fetchNdcPuneCoord() {
		final String uri = COORD_URL+"/fetchNdcPuneCoord";
		return restTemplate.getForObject(uri, String.class);

	}
	
	public List<String> fetchPunjabCoords(String district) {
		String uri = COORD_URL+"/fetchPunjabCoordinators?district=" + district;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	public List<String> fetchHimachalCoords(String department) {
		String uri = COORD_URL+"/fetchHimachalCoords?department=" + department;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}
	
	public String fetchHimachalDa() {
		String uri = COORD_URL+"/fetchHimachalDa";
		return restTemplate.getForObject(uri, String.class);
	}
	
	public Set<String> fetchDAs(OrganizationBean organizationDetails) {
		String uri = COORD_URL + "/fetchDAs";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		System.out.println("INSIDE ::::::::::: FETCH da");
		HttpEntity<OrganizationBean> entity = new HttpEntity<>(organizationDetails, headers);
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
				
				new ParameterizedTypeReference<List<String>>() {
				});
		return new java.util.HashSet<>(response.getBody());
	}
	
	public Set<String> fetchCoordinators(OrganizationBean organizationDetails) {
		String uri = COORD_URL + "/fetchCoordinators";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		System.out.println("INSIDE ::::::::::: FETCH COORDINATORS");
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<OrganizationBean> entity = new HttpEntity<>(organizationDetails, headers);
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
				new ParameterizedTypeReference<List<String>>() {
				});
		return new java.util.HashSet<>(response.getBody());
	}
	
	public Boolean isGovEmployee(String mail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlToAuthenticate = LDAP_URL+"/isEmailAvailable";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mail", mail);
		HttpEntity<Boolean> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity,
				Boolean.class);
		return response.getBody();
	}
	
	public Boolean isNicEmployee(String mail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlToAuthenticate = LDAP_URL+"/isNicEmployee";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate)
				.queryParam("mail", mail);
		HttpEntity<Boolean> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity,
				Boolean.class);
		return response.getBody();
	}

	public ProfileDto fetchProfileByEmailInBean(String email){
		final String uri = PROFILE_URL+"/fetchInBean?email=" + email;
		return restTemplate.getForObject(uri, ProfileDto.class);
	}
	
	public String findByMobile(String mobile) {
		final String uri = LDAP_URL+"/findByMobile?mobile=" + mobile;
		return restTemplate.getForObject(uri, String.class);
	}
	
//	public String isMobileAvailable(String mobile) {
//		final String uri = LDAP_URL+"/findByMobile?mobile=" + mobile;
//		return restTemplate.getForObject(uri, String.class);
//	}
	
	public String allLdapValues(String email) {
		final String uri = LDAP_URL+"/findByUidOrMailOrEquivalent?mail=" + email;
		return restTemplate.getForObject(uri, String.class);
	}
	
	public MobileAndName fetchMobileAndNameFromLdap(String email) {
		final String uri = LDAP_URL+"/fetchMobileAndName?mail=" + email;
		return restTemplate.getForObject(uri, MobileAndName.class);
	}
	
	public Boolean isEditable(String regNumber, String email) {
		final String uri = REPORTING_URL+"/isEditable?email=" + email +"&regNumber="+regNumber;
		return restTemplate.getForObject(uri, Boolean.class);
	}
	
	public FinalAuditTrack fetchFinalAuditTrack(String regNumber) {
		final String uri = REPORTING_URL+"/fetchFinalAuditTrack?regNumber=" + regNumber;
		return restTemplate.getForObject(uri, FinalAuditTrack.class);
	}
	
	public List<Status> fetchStatusTable(String regNumber) {
		final String uri = REPORTING_URL+"/fetchStatusTable?regNumber=" + regNumber;
		ResponseEntity<List<Status>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Status>>() {
				});
		return response.getBody();
	}

	public List<String> aliases(String email) {
		String uri = LDAP_URL+"/fetchAliasesAlongWithPrimary?mail=" + email;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}
	
	public Boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		Map<String, Object> data = new HashMap<>();
		data.put("status", status);
		data.put("finalAuditTrack", finalAuditTrack);
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(data, headers);
		String url = REPORTING_URL + "/updateStatusAndFinalAuditTrack";
		ResponseEntity<Boolean> response = restTemplate.postForEntity(url, entity, Boolean.class);
		//return send(data);
		
		if (response.getStatusCode() == HttpStatus.OK) {
		    System.out.println("Request Successful");
		    System.out.println(response.getBody());
		} else {
		    System.out.println("Request Failed");
		    System.out.println(response.getStatusCode());
		}
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
	
	public String validateOrganization(OrganizationDto organizationDetails) {
		String uri = ORGANIZATION_URL+"/validateOrganization";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<OrganizationDto> entity = new HttpEntity<>(organizationDetails, headers);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
				new ParameterizedTypeReference<String>() {
				});
		return response.getBody();
	}
	
	
	public String ValidateMobile(String mobile) {
		String msg = "";
		try {
			if ((!mobile.startsWith("+91")) && (!mobile.matches("^[0-9]{10}$"))) {
				msg = "Mobile not in correct Format";
			} else {
				if ((mobile.startsWith("+91") || mobile.startsWith("91")) && (!mobile.matches("^[+0-9]{13}$"))) {
					msg = "Mobile not in correct Format";
				} else if ((!mobile.startsWith("+91") || !mobile.startsWith("91"))
						&& !mobile.matches("^[+0-9]{8,15}$")) {
					msg = "Mobile not in correct Format";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return msg;
	}
	
	
	
	
	
//	public String ValidateMobileInLdap(String oldmobile, String mobile) {
//		String msg = "";
//		try {
//			String mobileInldap = findByMobile(mobile);
//			if (mobile.equals(mobileInldap)) {
//				msg = "Mobile already exists in Ldap";
//			} else if (mobile.equals(oldmobile)) {
//				msg = "Old Mobile and New mobile cannot be same";
//			}
//			
//			System.out.println("MSSSGGGGG ::::::::: " + msg);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return msg;
//	}

	
	public boolean IsMobileAvailableInLdap(String oldmobile, String mobile) {
		
	boolean isAvailable = false;
		try {
			
			mobile = mobile.trim();

			System.out.println("MOBILEEE ::::::::: " + mobile);
			
			final String uri = LDAP_URL+"/isMobileAvailable?mobile="+ mobile;
			boolean mobileInldap = restTemplate.getForObject(uri, boolean.class);
			
			if(mobileInldap) {
				isAvailable = true;
				
			}
			 else if (mobile.equals(oldmobile)) {
				isAvailable = true;
			}
			
			System.out.println("IS-AVAILABLEE ::::::::: " + isAvailable);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isAvailable;
	}

	   public Integer random() {
	        Integer response = 0;
	        SecureRandom rand = new SecureRandom();
	        int num1 = rand.nextInt(10);
	        while (num1 == 0) {
	            num1 = rand.nextInt(10);
	        }
	        int num2 = rand.nextInt(10);
	        int num3 = rand.nextInt(10);
	        int num4 = rand.nextInt(10);
	        int num5 = rand.nextInt(10);
	        int num6 = rand.nextInt(10);
	        response = num1 * 100000 + num2 * 10000 + num3 * 1000 + num4 * 100 + num5 * 10 + num6;
	        return response;
	    }
	
	   
	   
	   public FinalAuditTrack initializeFinalAuditTrackTable(String ip, String email, String formType, String remarks,
				String mobile, String name, String role, String regNumber) {
			FinalAuditTrack finalAuditTrack = null;
			if (regNumber == null || (regNumber != null && regNumber.isEmpty())) {
				finalAuditTrack = new FinalAuditTrack();
			} else {
				finalAuditTrack = fetchFinalAuditTrack(regNumber);
			}
			LocalDateTime currentTime = LocalDateTime.now();
			switch (role) {
			case "user":
				if (finalAuditTrack != null) {
					finalAuditTrack = new FinalAuditTrack();
					finalAuditTrack.setFormName(formType);
					finalAuditTrack.setApplicantIp(ip);
					finalAuditTrack.setOnHold("n");
					finalAuditTrack.setApplicantRemarks(remarks);
					finalAuditTrack.setApplicantMobile(mobile);
					finalAuditTrack.setApplicantName(name);
					finalAuditTrack.setApplicantEmail(email);
					finalAuditTrack.setApplicantDatetime(currentTime);
					finalAuditTrack.setToDatetime(currentTime);
				}
				break;
			case "ro":
				if (finalAuditTrack != null) {
					finalAuditTrack.setCaIp(ip);
					finalAuditTrack.setCaRemarks(remarks);
					finalAuditTrack.setCaMobile(mobile);
					finalAuditTrack.setCaName(name);
					finalAuditTrack.setCaEmail(email);
					finalAuditTrack.setCaDatetime(currentTime);
					finalAuditTrack.setToDatetime(currentTime);
				}
				break;
			case "coordinator":
				if (finalAuditTrack != null) {
					finalAuditTrack.setCoordinatorIp(ip);
					finalAuditTrack.setCoordinatorRemarks(remarks);
					finalAuditTrack.setCoordinatorMobile(mobile);
					finalAuditTrack.setCoordinatorName(name);
					finalAuditTrack.setCoordinatorEmail(email);
					finalAuditTrack.setCoordinatorDatetime(currentTime);
					finalAuditTrack.setToDatetime(currentTime);
				}
				break;
			case "support":
				if (finalAuditTrack != null) {
					finalAuditTrack.setSupportIp(ip);
					finalAuditTrack.setSupportRemarks(remarks);
					finalAuditTrack.setSupportMobile(mobile);
					finalAuditTrack.setSupportName(name);
					finalAuditTrack.setSupportEmail(email);
					finalAuditTrack.setSupportDatetime(currentTime);
					finalAuditTrack.setToDatetime(currentTime);
				}
				break;
			case "admin":
				if (finalAuditTrack != null) {
					finalAuditTrack.setAdminIp(ip);
					finalAuditTrack.setAdminRemarks(remarks);
					finalAuditTrack.setAdminMobile(mobile);
					finalAuditTrack.setAdminName(name);
					finalAuditTrack.setAdminEmail(email);
					finalAuditTrack.setAdminDatetime(currentTime);
					finalAuditTrack.setToDatetime(currentTime);
				}
				break;
			default:
				break;
			}
			return finalAuditTrack;
		}

		public FinalAuditTrack initializeFinalAuditTrackTableForReverting(String ip, String email, String formType,
				String remarks, String mobile, String name, String role, String regNumber) {
			FinalAuditTrack finalAuditTrack = null;
			if (regNumber == null || (regNumber != null && regNumber.isEmpty())) {
				finalAuditTrack = new FinalAuditTrack();
			} else {
				finalAuditTrack = fetchFinalAuditTrack(regNumber);
			}
			LocalDateTime currentTime = LocalDateTime.now();
			switch (role) {
			case "ro":
				if (finalAuditTrack != null) {
					finalAuditTrack.setCaIp(null);
					finalAuditTrack.setCaRemarks(null);
					finalAuditTrack.setCaMobile(null);
					finalAuditTrack.setCaName(null);
					finalAuditTrack.setCaEmail(null);
					finalAuditTrack.setCaDatetime(null);
					finalAuditTrack.setToDatetime(currentTime);
				}
				break;
			case "coordinator":
				if (finalAuditTrack != null) {
					finalAuditTrack.setCoordinatorIp(null);
					finalAuditTrack.setCoordinatorRemarks(null);
					finalAuditTrack.setCoordinatorMobile(null);
					finalAuditTrack.setCoordinatorName(null);
					finalAuditTrack.setCoordinatorEmail(null);
					finalAuditTrack.setCoordinatorDatetime(null);
					finalAuditTrack.setToDatetime(currentTime);
				}
				break;
			case "support":
				if (finalAuditTrack != null) {
					finalAuditTrack.setSupportIp(null);
					finalAuditTrack.setSupportRemarks(null);
					finalAuditTrack.setSupportMobile(null);
					finalAuditTrack.setSupportName(null);
					finalAuditTrack.setSupportEmail(null);
					finalAuditTrack.setSupportDatetime(null);
					finalAuditTrack.setToDatetime(currentTime);
				}
				break;
			case "admin":
				if (finalAuditTrack != null) {
					finalAuditTrack.setAdminIp(null);
					finalAuditTrack.setAdminRemarks(null);
					finalAuditTrack.setAdminMobile(null);
					finalAuditTrack.setAdminName(null);
					finalAuditTrack.setAdminEmail(null);
					finalAuditTrack.setAdminDatetime(null);
					finalAuditTrack.setToDatetime(currentTime);
				}
				break;
			default:
				break;
			}
			return finalAuditTrack;
		}

		public Status initializeStatusTable(String ip, String email, String formType, String remarks, String mobile,
				String name, String role) {
			Status status = new Status();
			status.setFormType(formType);
			status.setIp(ip);
			status.setSenderIp(ip);
			status.setOnholdStatus("n");
			status.setFinalId("");
			status.setRemarks(remarks);
			status.setSender(email);
			status.setSenderEmail(email);
			LocalDateTime currentTime = LocalDateTime.now();
			status.setCreatedon(currentTime);
			status.setSenderDatetime(currentTime);
			status.setSenderName(name);
			status.setSenderMobile(mobile);
			switch (role) {
			case "user":
				status.setSenderType(Constants.STATUS_USER_TYPE);
				break;
			case "ro":
				status.setSenderType(Constants.STATUS_CA_TYPE);
				break;
			case "coordinator":
				status.setSenderType(Constants.STATUS_COORDINATOR_TYPE);
				break;
			case "support":
				status.setSenderType(Constants.STATUS_SUPPORT_TYPE);
				break;
			case "admin":
				status.setSenderType(Constants.STATUS_ADMIN_TYPE);
				break;
			default:
				break;
			}
			return status;
		}	   

	
}
