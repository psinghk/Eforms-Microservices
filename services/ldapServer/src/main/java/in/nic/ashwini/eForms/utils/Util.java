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
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.Status;


@Service
public class Util {

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

	public HodDetailsDto getHodValues(String email) {
		final String uri = PROFILE_URL+"/fetchRoDetailsInBean?mail=" + email;
		return restTemplate.getForObject(uri, HodDetailsDto.class);

	}
	
	public String findDn(String email) {
		final String uri = LDAP_URL+"/fetchRoDetailsInBean?uid=" + email;
		return restTemplate.getForObject(uri, String.class);

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
		String uri = COORD_URL+"/fetchDAs";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<OrganizationBean> entity = new HttpEntity<>(organizationDetails, headers);
		ResponseEntity<Set<String>> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
				new ParameterizedTypeReference<Set<String>>() {
				});
		return response.getBody();
	}
	
	public Set<String> fetchCoordinators(OrganizationBean organizationDetails) {
		String uri = COORD_URL+"/fetchCoordinators";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<OrganizationBean> entity = new HttpEntity<>(organizationDetails, headers);
		ResponseEntity<Set<String>> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
				new ParameterizedTypeReference<Set<String>>() {
				});
		return response.getBody();
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

	public ProfileDto fetchProfileByEmailInBean(String email){
		final String uri = PROFILE_URL+"/fetchInBean?email=" + email;
		return restTemplate.getForObject(uri, ProfileDto.class);
	}
	
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
	
	
//	public String chkvalid(String cert,String audit,String ldap_id1,String ldap_id2) {
//		String msg="";
//			if (audit.equals("yes")) {
//				if (!cert.equals("true")) {
//                    msg= "Please upload Security audit clearance certificate in PDF format only";
//                }
//	    }else if (audit.equals("no")) {
//            if (ldap_id1.equals("") || ldap_id1 == null || ldap_id2.equals("") || ldap_id2 == null) {
//            	msg= "Enter Email Address [e.g: abc.xyz@zxc.com]";
//            } 
//        }
//		return msg;
//		}
	
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
	
	public FinalAuditTrack initializeFinalAuditTrackTableForReverting(String ip, String email, String formType, String remarks,
			String mobile, String name, String role, String regNumber) {
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
	
	
	  public boolean nameValidation(String fname) {
	        boolean flag = false;
	        if (fname.isEmpty() || fname.trim().equals("")) {
	            flag = true;
	        } else if (!fname.matches("^[a-zA-Z .,]{1,50}$")) {
	            flag = true;
	        }
	        return flag;
	    }
	  
	  public boolean pullurlValidation(String pull_url) {
	        boolean flag = false;
	        pull_url = pull_url.toLowerCase();
	        if (pull_url.isEmpty()) {
	            flag = true;
	        } else if (!pull_url.matches("^(?:(?:(?:https?|ftp):)?\\/\\/)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})).?)(?::\\d{2,5})?(?:[/?#]\\S*)?$")) {
	            flag = true;
	        }
	        return flag;
	    }
	  
	  public boolean baseipValidation(String baseip) {
	        boolean flag = false;
	        if (baseip == null || baseip.isEmpty()) {
	            flag = true;
	        } else if (!baseip.matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) {
	            flag = true;
	        } else if (baseip.startsWith("0") || baseip.equals("0.0.0.0") || baseip.equals("127.0.0.1") || baseip.equals("255.255.255.255") || baseip.endsWith("255")) {
	            flag = true;
	        }
	        return flag;
	    }
	  
	  public boolean serviceipValidation(String serviceip) {
	        boolean flag = false;
	        if (!serviceip.isEmpty()) {
	            if (!serviceip.matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) {
	                flag = true;
	            } else if (serviceip.startsWith("0") || serviceip.equals("0.0.0.0") || serviceip.equals("127.0.0.1") || serviceip.equals("255.255.255.255") || serviceip.endsWith("255")) {
	                flag = true;
	            }
	        }
	        return flag;
	    }
	  
	  public boolean addValidation(String add1) {
	        boolean flag = false;
	        if (add1.isEmpty()) {
	            flag = true;
	        } else if (!add1.matches("^[a-zA-Z#0-9\\s,.\\-\\/\\(\\)]{2,100}$")) {
	            flag = true;
	        }
	        return flag;
	    }
	  
	  public boolean EmailValidation(String email) {
	        boolean flag = false;
	        if (email.isEmpty()) {
	            flag = true;
	        }
	        else if (!email.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) {
	            flag = true;
	        }
	        return flag;
	    }
}
