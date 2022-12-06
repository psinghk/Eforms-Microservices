package in.nic.ashwini.eForms.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotBlank;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.HodDetailsDto;
import in.nic.ashwini.eForms.models.MobileAndName;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.Po;
import in.nic.ashwini.eForms.models.Po1;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.models.UserForCreate;
import in.nic.ashwini.eForms.models.UserForCreateForAppUsers;

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
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			protected boolean hasError(HttpStatus statusCode) {
				return false;
			}
		});
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
		final String uri = LDAP_URL + "/validateEmail?mail=" + email;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public HodDetailsDto getHodValues(String email) {
		final String uri = PROFILE_URL + "/fetchRoDetailsInBean?mail=" + email;
		return restTemplate.getForObject(uri, HodDetailsDto.class);

	}

	public String findDn(String email) {
		final String uri = LDAP_URL + "/fetchRoDetailsInBean?uid=" + email;
		return restTemplate.getForObject(uri, String.class);

	}

	public String fetchGemDAForFreeAccounts() {
		final String uri = COORD_URL + "/fetchGemDAForFreeAccounts";
		return restTemplate.getForObject(uri, String.class);

	}

	public String fetchGemDAForPaidAccounts() {
		final String uri = COORD_URL + "/fetchGemDAForPaidAccounts";
		return restTemplate.getForObject(uri, String.class);

	}

	public String fetchNdcPuneCoord() {
		final String uri = COORD_URL + "/fetchNdcPuneCoord";
		return restTemplate.getForObject(uri, String.class);

	}

	public List<String> fetchPunjabCoords(String district) {
		String uri = COORD_URL + "/fetchPunjabCoordinators?district=" + district;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	public List<String> fetchHimachalCoords(String department) {
		String uri = COORD_URL + "/fetchHimachalCoords?department=" + department;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	public String fetchHimachalDa() {
		String uri = COORD_URL + "/fetchHimachalDa";
		return restTemplate.getForObject(uri, String.class);
	}

	public Set<String> fetchDAs(OrganizationBean organizationDetails) {
		String uri = COORD_URL + "/fetchDAs";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
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
		String urlToAuthenticate = LDAP_URL + "/isEmailAvailable";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mail", mail);
		HttpEntity<Boolean> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity,
				Boolean.class);
		return response.getBody();
	}

	public Boolean isNicEmployee(String mail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlToAuthenticate = LDAP_URL + "/isNicEmployee";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mail", mail);
		HttpEntity<Boolean> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity,
				Boolean.class);
		return response.getBody();
	}

	public ProfileDto fetchProfileByEmailInBean(String email) {
		final String uri = PROFILE_URL + "/fetchInBean?email=" + email;
		return restTemplate.getForObject(uri, ProfileDto.class);
	}

	public String allLdapValues(String email) {
		final String uri = LDAP_URL + "/findByUidOrMailOrEquivalent?mail=" + email;
		return restTemplate.getForObject(uri, String.class);
	}

	public MobileAndName fetchMobileAndNameFromLdap(String email) {
		final String uri = LDAP_URL + "/fetchMobileAndName?mail=" + email;
		return restTemplate.getForObject(uri, MobileAndName.class);
	}

	public Boolean isEditable(String regNumber, String email) {
		final String uri = REPORTING_URL + "/isEditable?email=" + email + "&regNumber=" + regNumber;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public FinalAuditTrack fetchFinalAuditTrack(String regNumber) {
		final String uri = REPORTING_URL + "/fetchFinalAuditTrack?regNumber=" + regNumber;
		return restTemplate.getForObject(uri, FinalAuditTrack.class);
	}

	public List<Status> fetchStatusTable(String regNumber) {
		final String uri = REPORTING_URL + "/fetchStatusTable?regNumber=" + regNumber;
		ResponseEntity<List<Status>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Status>>() {
				});
		return response.getBody();
	}

	public List<String> aliases(String email) {
		String uri = LDAP_URL + "/fetchAliasesAlongWithPrimary?mail=" + email;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}
	//need to ask 
	public Boolean uidEmailValidate( String uid) {
		final String uri = LDAP_URL + "/findByUid?uid=" + uid;
		if(restTemplate.getForObject(uri, Object.class) == null) {
			return false;
		}
		return true;
	}
	
	//update by sunny for the admin part
	
	//find bo
	//find po
	//find domain
	
	public List<String> fetchDomains(String bo) {
		String uri = LDAP_URL + "/findDomains?bo="+bo  ;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}
	
	public List<Po1> fetchPos(String base) {
		List<Po1> temp1 = null;
		try
		{
		String uri = LDAP_URL + "/fetchPos?base="+base;
		ResponseEntity<List<Po1>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Po1>>() {
				});
		temp1 = response.getBody();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return temp1;
	}
	public List<Po> fetchBos(String base) {
		List<Po> temp=null;
		try
		{
		String uri = LDAP_URL + "/fetchBos?base="+base;
		ResponseEntity<List<Po>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Po>>() {
				});
		temp=response.getBody();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return temp;
	}
	
	public List<String> createForMailUsers(@RequestBody UserForCreate user, @RequestParam @NotBlank final String bo,
			@RequestParam @NotBlank final String parentBo) {
		String uri = LDAP_URL + "/createMailUsers?base=";
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}
	
	
	public List<String> createAppForUsers(@RequestBody UserForCreateForAppUsers user, @RequestParam @NotBlank final String bo,
			@RequestParam @NotBlank final String parentBo) {
		String uri = LDAP_URL + "/createAppUsers?base="+user+" "+bo+" "+ parentBo;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}
	
	public String findUid(String email) {
		final String uri = LDAP_URL + "/fetchUid?mail=" + email;
		return restTemplate.getForObject(uri, String.class);
	}
	public void sendObj(UserForCreate userForCreate, String bo, String parentBo) {
		final String uri = LDAP_URL + "/createMailUsers?bo=" + bo+"&parentBo="+parentBo;
		ResponseEntity<String> temp = restTemplate.postForEntity(uri, userForCreate, String.class);
		System.out.println(temp.getStatusCodeValue()+"||"+temp.getBody()+"MailUser--->");
	}
	public void sendObj1(UserForCreateForAppUsers userForCreateForAppUsers, String bo, String parentBo) {
		final String uri = LDAP_URL + "/createAppUsers?bo=" + bo+"&parentBo="+parentBo;
		ResponseEntity<String> temp = restTemplate.postForEntity(uri, userForCreateForAppUsers, String.class);
		System.out.println(temp.getStatusCodeValue()+"||"+temp.getBody()+"AppUser--->");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//update by sunny
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
			return response.getBody();
		}
			
			//end sunny
	

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
		// return send(data);

		if (response.getStatusCode() == HttpStatus.OK) {
			System.out.println("Request Successful");
			System.out.println(response.getBody());
		} else {
			System.out.println("Request Failed");
			System.out.println(response.getStatusCode());
		}
		return response.getBody();
	}

	/*
	 * public String dorValidation(String dor, String dob) { String msg = ""; if
	 * (dor.matches("([0-9]{2})[-][0-9]{2}[-][0-9]{4}")) { try { SimpleDateFormat
	 * formatter = new SimpleDateFormat("dd-MM-yyyy");
	 * 
	 * formatter.setLenient(false); Calendar cal1 = Calendar.getInstance(); Date dr;
	 * 
	 * dr = formatter.parse(dor);
	 * 
	 * cal1.setTime(dr); if (!dob.isEmpty()) { Date date1 = formatter.parse(dob);
	 * cal1.setTime(date1); long diff = formatter.parse(dor).getTime() -
	 * formatter.parse(dob).getTime(); long diffHours = diff / (60 * 60 * 1000);
	 * long days = (diffHours / 24); long years = (days / 365); if ((years) > 67 ||
	 * ((years) == 67) && (cal1.get(Calendar.MONTH) > cal1.get(Calendar.MONTH))) {
	 * msg = RETIREMENT_AGE_MESSAGE; } Date date2 = formatter.parse(dor); Calendar
	 * cal2 = Calendar.getInstance(); cal2.setTime(date2); int yr60 =
	 * (cal1.get(Calendar.YEAR)) + 49 + 1900; int yc = (cal2.get(Calendar.YEAR)) +
	 * 1900; int yt = (cal1.get(Calendar.YEAR)) + 1900; int mr1 =
	 * (cal1.get(Calendar.MONTH)) + 1; int mr = (cal2.get(Calendar.YEAR)) + 1; int
	 * d1 = (cal1.get(Calendar.DATE)); int d2 = (cal2.get(Calendar.YEAR)); if (yc <
	 * yt || yc > yr60) { msg = "Please enter Date Of retirement in correct format";
	 * } else if (yc == yt) { if (mr < mr1 || (mr == mr1 && d2 < d1)) msg =
	 * RETIREMENT_AGE_MESSAGE; } } } catch (ParseException e) { msg =
	 * e.getMessage(); } } else { msg =
	 * "Please enter Date Of retirement in correct format";
	 * 
	 * }
	 * 
	 * return msg; }
	 */
	 public String dorValidation(String dor, String dob) {
	        String msg = "";
	        if (dor.isEmpty()) {
	            msg = "Please Enter Date of Retirement";
	        } else if (!dor.matches("([0-9]{2})[-][0-9]{2}[-][0-9]{4}")) {
	            msg = "Please select Date of Retirement in correct format";
	        } else //                    dor = yearrt + "-" + monthrt + "-" + dayrt;
	        {
	            dor = dor.trim();
	            if (dor.matches("([0-9]{2})[-][0-9]{2}[-][0-9]{4}")) {
	                Date d11 = null;
	                Date d12 = null;

	                DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
	                java.util.Date date = new java.util.Date();
	                format.setLenient(false);
	                String dateStart = dob;
	                String dateEnd = dor;
	                int month = 0;
	                int dob_month = 0;
	                try {
	                    Calendar cal = Calendar.getInstance();
	                    Date dr = format.parse(dor);
	                    cal.setTime(dr);
	                    month = cal.get(Calendar.MONTH);
	                    //String month = dor.substring(3, 5);
	                    //Integer month_new = Integer.parseInt(month);
	                    if (!dob.isEmpty()) {
	                        Date db = format.parse(dob);
	                        cal.setTime(db);
	                        dob_month = cal.get(Calendar.MONTH);
	                        try {

	                            d11 = format.parse(dateStart);
	                            d12 = format.parse(dateEnd);
	                        } catch (ParseException e) {
	                            e.printStackTrace();
	                        }
	                        long diff = d12.getTime() - d11.getTime();
	                        long diffSeconds = diff / 1000;
	                        long diffMinutes = diff / (60 * 1000);
	                        long diffHours = diff / (60 * 60 * 1000);
	                        long days = (diffHours / 24);
	                        long years = (days / 365);
	                        //if ((years) > 65 || ((years) == 65) && (Integer.parseInt(month) > Integer.parseInt(dob_month))) {
	                        //if ((years) > 66 || ((years) == 66) && (month > dob_month)) 
	                        if ((years) > 67 || ((years) == 67) && (month > dob_month)) // line modified by pr on 2ndaug18
	                        {
	                            msg = "year of retirement can not exceed 67 years from the DOB year"; // 66 -> 67 by pr on 2ndaug18
	                        } else {
	                            msg = "";
	                        }
	                        String pdate = format.format(date);
	                        java.util.Date date1 = format.parse(pdate);
	                        java.util.Date date2 = format.parse(dor);

	                        //int yr60 = date1.getYear() + 48 + 1900; 
	                        int yr60 = date1.getYear() + 49 + 1900;  // to add in the current year to make it 67 , modified by pr on 2ndaug18

	                        int yc = date2.getYear() + 1900;
	                        int yt = date1.getYear() + 1900;
	                        int mr1 = date1.getMonth() + 1;
	                        int mr = date2.getMonth() + 1;
	                        int d1 = date1.getDate();
	                        int d2 = date2.getDate();
	                        if (yc < yt || yc > yr60) {
	                            msg = "Please enter Date Of retirement in correct format";

	                        } else if (yc == yt) {
	                            if (mr < mr1) {
	                                msg = "year of retirement can not exceed 67 years from the DOB year"; // 66 -> 67 done by pr on 2ndaug18
	                            } else if (mr == mr1) {
	                                if (d2 < d1) {
	                                    msg = "year of retirement can not exceed 67 years from the DOB year"; // 66 -> 67 done by pr on 2ndaug18
	                                }
	                            }
	                        }
	                    } else {

	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	        return msg;

	    }
	
	
	
	

	public String dobValidation(String value) {
		String msg = "";
		if (value.matches("([0-9]{2})[-][0-9]{2}[-][0-9]{4}")) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
				String pdate = formatter.format(new Date());
				Date date1;
				date1 = formatter.parse(pdate);
				Date date2 = formatter.parse(value);
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(date1);
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(date2);
				msg = validateDate(cal1, cal2);
			} catch (ParseException e) {
				msg = e.getMessage();
			}
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
		String uri = ORGANIZATION_URL + "/validateOrganization";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<OrganizationDto> entity = new HttpEntity<>(organizationDetails, headers);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
				new ParameterizedTypeReference<String>() {
				});
		return response.getBody();
	}

	// findByUid
	public boolean findByUid(String uid) {
		final String uri = LDAP_URL + "/findByUid?uid=" + uid;
		return restTemplate.getForObject(uri, Boolean.class);
		// return uid;

	}
	public boolean nameValidation(String fname) {
        boolean flag = false;
        if (fname.isEmpty() || fname.trim().equals("")) {
            flag = true;
        } else if (fname.matches("^[a-zA-Z .,]{1,50}$")) {
            flag = true;
        }
        return flag;
    }
	public boolean applicantMobileValidation(String mobile) {
        boolean flag = false;
        if (mobile.isEmpty()) {
            flag = true;
        } else {
            if (mobile.matches("^[+0-9]{13}$")) {
                flag = true;
            } 
        }
        return flag;
    }
	public boolean addstateValidation(String user_state) {
        boolean flag = false;
        if (user_state == null || user_state.isEmpty()) {
            flag = true;

        } else if (user_state.matches("^[a-zA-Z0-9\\.\\-\\_ ]{1,100}$")) {
            flag = true;
        }
        return flag;
    }
	 public boolean desigValidation(String desig) {
	        boolean flag = false;
	        boolean numeric = desig.matches("-?\\d+(\\.\\d+)?");
	        if (desig.isEmpty()) {
	            flag = true;
	        } else {
	            if (numeric) {
	                flag = true;
	            } else {
	                if (desig.matches("^[a-zA-Z0-9 .,\\-\\_\\&]{2,100}$")) {
	                    flag = true;
	                } else {
	                }
	            }
	        }
	        return flag;
	    }
	 public boolean employcodevalidation(String employcode) {
	        boolean flag = false;
	        if (!employcode.isEmpty()) {
	            if (employcode.matches("^[a-zA-_Z0-9]{2,12}$")) {
	                flag = true;
	            }
	        }
	        return flag;
	    }
	 public boolean EmailValidation(String email) {
	        boolean flag = false;
	        if (email.isEmpty()) {
	            flag = true;
	        } else if (email.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) {
	            flag = true;
	        }
	        return flag;
	    }
	 
	 
}
