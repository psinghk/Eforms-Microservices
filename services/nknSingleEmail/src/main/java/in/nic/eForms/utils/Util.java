package in.nic.eForms.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.HodDetailsDto;
import in.nic.eForms.models.MobileAndName;
import in.nic.eForms.models.OrganizationBean;
import in.nic.eForms.models.OrganizationDto;
import in.nic.eForms.models.PreviewFormBean;
import in.nic.eForms.models.ProfileDto;
import in.nic.eForms.models.Status;
import in.nic.eForms.models.UserForCreate;
import in.nic.eForms.models.UserForSearch;


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

	
//	public List<String> findDomains(String bo) {
//		final String uri = LDAP_URL+"/findDomains?bo=" + bo;
//		return restTemplate.getForObject(uri, String.class);
//
//	}
	
	
	public List<String> findDomains(String bo) {
		String uri = LDAP_URL+"/findDomains?bo=" + bo;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
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
	
	public UserForSearch findByMobile(String mobile) {
		final String uri = LDAP_URL+"/findByMobile?mobile=" + mobile;
		return restTemplate.getForObject(uri, UserForSearch.class);
	}
	
	public Boolean isEditable(String regNumber, String email) {
		final String uri = REPORTING_URL+"/isEditable?email=" + email +"&regNumber="+regNumber;
		return restTemplate.getForObject(uri, Boolean.class);
	}
	
//	public Boolean createMailUsers(UserForCreate user, String po, String bo) {
//		System.out.println("Inside createMailUser111");
//		
//		final String uri = LDAP_URL+"/createMailUsers?user=" + user +"&bo="+ bo.trim() +"&parentBo="+po.trim() ;
//		
//		System.out.println("Inside createMailUser111 uri::::::::"+uri);
//		
//		Object obj = restTemplate.getForObject(uri, Object.class);
//		
//		System.out.println(obj);
//		
//		return restTemplate.getForObject(uri, boolean.class);
//	}
	
	public Boolean createMailUsers(UserForCreate userForCreate, String bo, String parentBo) {
		final String uri = LDAP_URL + "/createMailUsers?bo=" + bo+"&parentBo="+parentBo;
		ResponseEntity<String> temp = restTemplate.postForEntity(uri, userForCreate, String.class);
		System.out.println(temp.getStatusCodeValue()+"||"+temp.getBody());
		if(temp.getBody().equalsIgnoreCase("true")) {
			return true;
		}else {
			return false;
		}
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
	
//	public String validationFinalId(String value) throws ParseException {
//		String msg = "";
//		if (!value.matches("^[a-zA-Z0-9\\.\\-]*$")) {
//			msg = "Please enter valid Final ID";
//		}else if(value.indexOf(".") == -1 && value.indexOf("-") == -1) {
//			msg += " UID " + value + " must contain a dot(.) or hyphen(-) or both ";
//		} else if (isGovEmployee(value)) {
//			msg = " UID " + value + " is available ";
//		}
//		return msg;
//	}
//	
//	public String validationPrimaryId(String value) throws ParseException {
//		String msg = "";
//		if (!value.matches("^[a-zA-Z0-9\\\\.\\\\-]*$")) {
//			msg = "Please enter valid Primary ID";
//		} 
//		return msg;
//	}
//	
//	public String validationPo(String value) throws ParseException {
//		String msg = "";
//		if (!value.matches("^[a-zA-Z#0-9_\\\\s,'.\\\\-\\\\/\\\\(\\\\)]{2,150}$")) {
//			msg = "Please enter valid PO";
//		} 
//		return msg;
//	}
//	
//	public String validationBo(String value) throws ParseException {
//		String msg = "";
//		if (!value.matches("^[a-zA-Z#0-9_\\\\s,'.\\\\-\\\\/\\\\(\\\\)]{2,150}$")) {
//			msg = "Please enter valid BO";
//		} 
//		return msg;
//	}
//	
//	public String validationDomain(String value) throws ParseException {
//		String msg = "";
//		if (!value.matches("^[a-zA-Z#0-9_\\\\s,'.\\\\-\\\\/\\\\(\\\\)]{2,150}$")) {
//			msg = "Please enter valid Domain";
//		} 
//		return msg;
//	}
//	
//	
//	public String validationemail(String value) throws ParseException {
//		String msg = "";
//		if (value.matches("^[\\\\\\\\w\\\\\\\\-\\\\\\\\.\\\\\\\\+]+@[a-zA-Z0-9\\\\\\\\.\\\\\\\\-]+.[a-zA-z0-9]{2,4}$")) {
//			msg = "Enter Email Address [e.g: abc.xyz@gov.in";
//		} 
//		return msg;
//	}
//	
	
//	 public String validUID(String uid) {
//	        System.out.println(" inside onlyuid function uid is " + uid);
//	        String valid = "false";
//	        String errorMsg = "";
//	        boolean right = true;
//	        if (uid == null || uid.equals("")) {
//	            System.out.println(" uid not null ");
//	            right = false;
//	            errorMsg = "Userid can not be blank.";
//	        } else if (!(uid + "@nic.in").matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) // else if added by pr on 3rdapr18
//	        {
//	            right = false;
//	            errorMsg = "Userid is Invalid.";
//	        } else if (Character.isDigit(uid.charAt(0))) {
//	            System.out.println(" uid is digit ");
//	            right = false;
//	            errorMsg = "Userid can not start with a numeric value.";
//	        } else if (uid.contains("_")) {
//	            System.out.println(" uid contains _ ");
//	            right = false;
//	            errorMsg = "Userid can not contain underscore[_].";
//	        } else if (uid.endsWith(".") || uid.endsWith("-") || uid.startsWith(".") || uid.startsWith("-")) {
//	            System.out.println(" uid ends with .");
//	            right = false;
//	            errorMsg = "Userid can not start or end with dot[.] and hyphen[-]. ";
//	        } else {
//	            System.out.println(" uid fine ");
//	            boolean uflag = false;
//	            for (int l = 0; l < uid.length(); l++) {
//	                System.out.println(" inside for ");
//	                char a = uid.charAt(l);
//	                if (a == '.') {
//	                    System.out.println(" inside a is dot");
//	                    char b = uid.charAt(l + 1);
//	                    char c = '-';
//	                    if ((a == b) || (b == c)) {
//	                        System.out.println(" inside a=b ");
//	                        uflag = true;
//	                        valid = "false";
//	                    }
//	                }
//	                if (a == '-') {
//	                    System.out.println(" inside a is  - ");
//	                    char b = uid.charAt(l + 1);
//	                    char c = '.';
//	                    if ((a == b) || (b == c)) {
//	                        System.out.println(" inside a=b ");
//	                        uflag = true;
//	                        valid = "false";
//	                    }
//	                }
//	            }
//	            System.out.println(" uid fine uflag is " + uflag);
//	            if (uflag) {
//	                right = false;
//	                errorMsg = "Userid contains continuous dot[.] or hyphen[-].";
//	            }
//	        }
//	        if (uid.length() < 8 || uid.length() > 20) {
//	            System.out.println(" uid length is " + uid.length());
//	            right = false;
//	            errorMsg = "Userid can not be less than 8 characters or more than 20 characters.";
//	        }
//	        System.out.println(" inside onlyuid function at the end valid value is " + valid + " error msg value is " + errorMsg);
//	        if (right) {
//	            valid = "true";
//	        } else {
//	            valid = "false";
//	        }
//	       
//	        return errorMsg;
//	    }
	   
	   public List<String> fetchDomainsByCatAndMinAndDep(String empCategory,String ministry,String empDept) {
			String uri = COORD_URL + "/fetchDomainsByCatAndMinAndDep?empCategory=" + empCategory +"&ministry="+ministry+"&empDept="+empDept;
			ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<String>>() {
					});
			return response.getBody();
		}
	   
		public List<String> fetchDomainsByCatAndMin(String empCategory,String ministry) {
			String uri = COORD_URL + "/fetchDomainsByCatAndMin?empCategory=" + empCategory +"&ministry="+ministry;
			ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<String>>() {
					});
			return response.getBody();
		}
	 
		
		
		public List<String> fetchBoByMinistry(String empCategory,String ministry,String empDept) {
			String uri = COORD_URL + "/fetchBoByMinistry?empCategory=" + empCategory +"&ministry="+ministry+"&empDept="+empDept;
			System.out.println("::::::::::uri:::::::::::::::::"+uri);
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
		
		public List<String> fetchdistDomain() {
			String uri = COORD_URL + "/fetchdistDomain";
			ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<String>>() {
					});
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
		
		public List<String> emailsAgainstMobile(String mobile) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			final String uri = LDAP_URL + "/fetchEmailsAgainstMobile?mobile=" + mobile;
			System.out.println("count the number of mobile="+uri);
			//UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri).queryParam("mobile", mobile);
			ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<String>>() {
					});
			System.out.println("count the number of mobile response="+response);
			return response.getBody();
		}
	 
	   
		 public String checkAvailableEmail(String mobile) {
			 String errorMsg = "";
		   Set<String> emailsAgainstMobile=new HashSet<String>(); 
		   System.out.println("mobile:::::::" + mobile);
		   HashMap<String, Object> values=new HashMap<String, Object>();
		   
		   List<String> ldapemailsAgainstMobile= emailsAgainstMobile(mobile);
		   
		   for (String x : ldapemailsAgainstMobile) {
			   emailsAgainstMobile.add(x); 
		   }
		   System.out.println("emailsAgainstMobile:::::::" + emailsAgainstMobile);
		   
			  if (emailsAgainstMobile.size() > 3) {
				  System.out.println("emailsAgainstMobile:::::1::" +emailsAgainstMobile.size());
				  errorMsg= "There are already 3 email addresses registered against your mobile number (" + mobile + ")";
			  } else if (emailsAgainstMobile.size() == 3) {
		    	  System.out.println("emailsAgainstMobile:::::2::" );
		          if (emailsAgainstMobile.contains("-admin")) {
		        	  System.out.println("emailsAgainstMobile:::::3::" );
		        	  errorMsg= "There are already 3 email addresses registered against your mobile number (" + mobile + ")";
		          } else {
		        	  System.out.println("emailsAgainstMobile:::::4::" );
		        	  errorMsg= "There are already 3 email addresses registered against your mobile number (" + mobile + ")";
		          }
		      }
			return errorMsg;
		   
	   }
		 
		 
		 public FinalAuditTrack initializeFinalAuditTrackTable(String ip, String email, String formType, String remarks,
					String mobile, String name, String role, String regNumber) {
			 System.out.println("::::::::::ip:::::::::::::::::"+ip);
				System.out.println("::::::::::email:::::::::::::::::"+email);
				System.out.println("::::::::::formType:::::::::::::::::"+formType);
				System.out.println("::::::::::remarks:::::::::::::::::"+remarks);
				System.out.println("::::::::::mobile:::::::::::::::::"+mobile);
				System.out.println("::::::::::name:::::::::::::::::"+name);
				System.out.println("::::::::::role:::::::::::::::::"+role);
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
				System.out.println("::::::::::ip:::::::::::::::::"+ip);
				System.out.println("::::::::::email:::::::::::::::::"+email);
				System.out.println("::::::::::formType:::::::::::::::::"+formType);
				System.out.println("::::::::::remarks:::::::::::::::::"+remarks);
				System.out.println("::::::::::mobile:::::::::::::::::"+mobile);
				System.out.println("::::::::::name:::::::::::::::::"+name);
				System.out.println("::::::::::role:::::::::::::::::"+role);
				Status status = new Status();
				status.setFormType(formType);
				System.out.println("::::::::::1:::::::::::::::::");
				status.setIp(ip);
				System.out.println("::::::::::2:::::::::::::::::");
				status.setSenderIp(ip);
				System.out.println("::::::::::3:::::::::::::::::");
				status.setOnholdStatus("n");
				System.out.println("::::::::::4:::::::::::::::::");
				status.setFinalId("");
				System.out.println("::::::::::5:::::::::::::::::");
				status.setRemarks(remarks);
				System.out.println("::::::::::6:::::::::::::::::");
				status.setSender(email);
				System.out.println("::::::::::7:::::::::::::::::");
				status.setSenderEmail(email);
				System.out.println("::::::::::8:::::::::::::::::");
				LocalDateTime currentTime = LocalDateTime.now();
				System.out.println("::::::::::9:::::::::::::::::");
				status.setCreatedon(currentTime);
				System.out.println("::::::::::10:::::::::::::::::");
				status.setSenderDatetime(currentTime);
				System.out.println("::::::::::11:::::::::::::::::");
				status.setSenderName(name);
				System.out.println("::::::::::12:::::::::::::::::");
				status.setSenderMobile(mobile);
				System.out.println("::::::::::13:::::::::::::::::");
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
				System.out.println("::::::::::ip1:::::::::::::::::"+ip);
				System.out.println("::::::::::email1:::::::::::::::::"+email);
				System.out.println("::::::::::formType1:::::::::::::::::"+formType);
				System.out.println("::::::::::remarks1:::::::::::::::::"+remarks);
				System.out.println("::::::::::mobile1:::::::::::::::::"+mobile);
				System.out.println("::::::::::name1:::::::::::::::::"+name);
				System.out.println("::::::::::role1:::::::::::::::::"+role);
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
	   
			
			
			public boolean dorValidation(LocalDate sdor, LocalDate sdob) {
				
				System.out.println("Single_dob:::::::"+sdob);
				System.out.println("Single_dob:::::::"+sdor);
				
				Date dobdate = java.sql.Date.valueOf(sdob);
				Date dordate = java.sql.Date.valueOf(sdor);
				Calendar dob = Calendar.getInstance();
				Calendar dor = Calendar.getInstance();
				Calendar future = Calendar.getInstance();
		        Calendar past = Calendar.getInstance();
				dob.setTime(dobdate);
				future.setTime(dobdate);
				past.setTime(dobdate);
				System.out.println("dob::::::"+dob);//2000
				dor.setTime(dordate);
				System.out.println("dor::::::"+dor);
				future.add(Calendar.YEAR, 67);//2067
				System.out.println("future::::::"+future);
				past.add(Calendar.YEAR, 18);//2018
				System.out.println("past::::::"+past);
				if(dor !=null) {
					//dor 2071
					//past 2021
					//future 2070
					//dob 2003
					if((dor.equals(past)) || (dor.before(past)) || (dor.after(future)) || (dor.equals(future))){
						System.out.println("not valid dorrr");
						return false;
					}else {
						System.out.println("valid dorrr");
						return true;
					}
				}else {
					System.out.println("valid dorrr");
					return true;
				}
			}
			
			
			public boolean dobValidation( LocalDate sdob) {
		        Date dobDate = java.sql.Date.valueOf(sdob);
		        Calendar cheque = Calendar.getInstance();
		        cheque.setTime(dobDate);//2000
		        Calendar mindate = Calendar.getInstance();
		        mindate.add(Calendar.YEAR, -18);
		        
		        System.out.println("mindate:::::"+mindate);
		        
		        if(cheque !=null) {
		        	 if(!cheque.equals(mindate) || !cheque.before(mindate)) {
				        	System.out.println("valid dob");
				         return true;
				        }else {
				        	return false;
				        }
		        }else {
		        	return false;
		        }
		       
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
			 
			 
			 public boolean institueIdValidation(String insid) {
			        boolean flag = false;
			        if (insid.isEmpty()) {
			        } else if (!insid.matches("^[a-zA-Z0-9 ,.-]{1,50}$")) {
			            flag = true;
			        }
			        return flag;
			    }
			 
			 
			 public boolean EmailValidation(String email) {
			        boolean flag = false;
			        if (email.isEmpty()) {
			            flag = true;
			        } else if (!email.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) {
			            flag = true;
			        }
			        return flag;
			    }
			 
			 
			    public boolean po(String po) {
			        boolean flag = false;
			        if (po.isEmpty() || po.trim().equals("")) {
			            flag = true;
			        } else if (!po.matches("^[a-zA-Z#0-9\\\\s,'.\\\\-\\\\/\\\\(\\\\)]{2,150}$")) {
			            flag = true;
			        }
			        return flag;
			    }
			    
			    
			    public boolean bo(String bo) {
			        boolean flag = false;
			        if (bo.isEmpty() || bo.trim().equals("")) {
			            flag = true;
			        } else if (bo.matches("^[a-zA-Z#0-9_\\\\\\\\\\\\\\\\s,'.\\\\\\\\\\\\\\\\-\\\\\\\\\\\\\\\\/\\\\\\\\\\\\\\\\(\\\\\\\\\\\\\\\\)]{2,150}$")) {
			            flag = true;
			        }
			        return flag;
			    }
			    
//			    public boolean domain(String domain) {
//			        boolean flag = false;
//			        if (domain.isEmpty() || domain.trim().equals("")) {
//			            flag = true;
//			        } else if (!domain.matches("^(?!:\\\\/\\\\/)([a-zA-Z0-9-\\\\_]+\\\\.){0,5}[a-zA-Z0-9-\\\\_][a-zA-Z0-9-\\\\_]+\\\\.[a-zA-Z]{2,64}?$")) {
//			            flag = true;
//			        }
//			        return flag;
//			    }
			    
			    public boolean domain(String add1) {
			        boolean flag = false;
			        if (add1.isEmpty()) {
			            flag = true;
			        } else if (!add1.matches("^[a-zA-Z#0-9\\s,.\\-\\/\\(\\)]{2,100}$")) {
			            flag = true;
			        }
			        return flag;
			    }
			    
			    
				public String isValid(String value) {
					String msg="";
					if (value == null || value.equals("")) {
						System.out.println("Userid can not be blank.");
						msg="Userid can not be blank.";
						//return false;
					} 
//					else if (!(value + "@nic.in").matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) // else if
//					{
//						System.out.println("Userid is Invalid.");
//						return false;
//						
//					}
//					else if (!value.matches("^[a-zA-Z0-9\\\\.\\\\-]*$")) {
//						System.out.println("Please enter valid Final ID");
//						System.out.println("Userid cannot contain whitespaces.");
//						return false;
//						
//					}
					
					else if (value.indexOf(".") == -1 && value.indexOf("-") == -1) {
						System.out.println(" must contain a dot(.) or hyphen(-) or both ");
						//return false;
						msg=" must contain a dot(.) or hyphen(-) or both ";
						
//					} else if (utilityService.isGovEmployee(value)) {
//						System.out.println(" UID " + value + " is available ");
//						return false;
						
					} else if (Character.isDigit(value.charAt(0))) {
						System.out.println("Userid can not start with a numeric value.");
						//return false;
						msg=" Userid can not start with a numeric value. ";
						
					} else if (value.contains("_")) {
						System.out.println(" uid contains _ ");
						System.out.println("Userid can not contain underscore[_].");
						//return false;
						msg=" Userid can not contain underscore[_].";
						
					} else if (value.endsWith(".") || value.endsWith("-") || value.startsWith(".") || value.startsWith("-")) {
						System.out.println(" uid ends with .");
						System.out.println("Userid can not start or end with dot[.] and hyphen[-]. ");
						//return false;
						msg=" Userid can not start or end with dot[.] and hyphen[-]. ";
						
					}if (value.length() < 8 || value.length() > 26) {
						System.out.println(" uid length is " + value.length());
						System.out.println("Userid can not be less than 8 characters or more than 20");
						//return false;
						msg=" Userid can not be less than 8 characters or more than 20 ";
						
						// characters.";
					} else {
						System.out.println(" uid fine ");
						//boolean uflag = false;
						for (int l = 0; l < value.length(); l++) {
							System.out.println(" inside for ");
							char a = value.charAt(l);
							if (a == '.') {
								System.out.println(" inside a is dot");
								char b = value.charAt(l + 1);
								char c = '-';
								if ((a == b) || (b == c)) {
									System.out.println(" inside a=b ");
									//uflag = true;
									msg=" Userid contains continuous dot[.] or hyphen[-]. ";
									// valid = "false";
								}
							}
							if (a == '-') {
								System.out.println(" inside a is  - ");
								char b = value.charAt(l + 1);
								char c = '.';
								if ((a == b) || (b == c)) {
									System.out.println(" inside a=b ");
									//uflag = true;
									msg=" Userid contains continuous dot[.] or hyphen[-]. ";
									// valid = "false";
								}
							}
						}
						//System.out.println(" uid fine uflag is " + uflag);
//						if (uflag) {
//							System.out.println("Userid contains continuous dot[.] or hyphen[-].");
//							return false;
//							
//						}else {
//							return true;
//						}
					}
					return msg;
					
					//return false;
				}
				
				
				public boolean primaryidValid(String primaryid) {
					
					if (primaryid != null) {
						if (!primaryid.isEmpty()) {
							if (!primaryid.matches("^[a-zA-Z0-9\\\\\\\\.\\\\\\\\-]*$")) {
								return false;
						        }else {
									return true;
								}
							} else {
								return true;
							}
						}
					return false;
				}
}
