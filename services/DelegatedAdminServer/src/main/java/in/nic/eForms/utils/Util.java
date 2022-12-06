package in.nic.eForms.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
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

import in.nic.ashwini.eForms.entities.UserTrailBean;
import in.nic.eForms.entities.User;
import in.nic.eForms.entities.UserAttributes;
import in.nic.eForms.models.AddAliasTrailBean;
import in.nic.eForms.models.ChangeIMAPandPOPTrailBean;
import in.nic.eForms.models.DeactivateTrailBean;
import in.nic.eForms.models.DeleteUserAccountTrailBean;
import in.nic.eForms.models.ExchangePrimaryEquivelantTrailBean;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.HodDetailsDto;
import in.nic.eForms.models.MobileAndName;
import in.nic.eForms.models.MoveToRetiredBOTrailBean;
import in.nic.eForms.models.OrganizationBean;
import in.nic.eForms.models.OrganizationDto;
import in.nic.eForms.models.ProfileDto;
import in.nic.eForms.models.Quota;
import in.nic.eForms.models.ResetPasswordTrailBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.models.SwapSupportContractorTrailBean;
import in.nic.eForms.models.UpdateDateOfExpiryTrailBean;
import in.nic.eForms.models.UserForCreate;
import in.nic.eForms.models.UserForCreateForAppUsers;
import in.nic.eForms.models.UserForSearch;
import in.nic.eForms.models.UserSearchBean;
import in.nic.eForms.models.editPersonalDetailslBean;
import in.nic.eForms.services.ValidationService;

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

	// by sunny
	public Boolean isMobileAvailableMoreThanThree1(String mobile) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlToAuthenticate = LDAP_URL + "/isMobileAvailableMoreThanThree";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mobile", mobile);
		HttpEntity<Boolean> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity,
				Boolean.class);
		return response.getBody();
	}

	public List<Quota> fetchServicePackageFromLdap(String bo) {
		String uri = LDAP_URL + "/fetchServicePackage?bo=" + bo;
		ResponseEntity<List<Quota>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Quota>>() {
				});
		return response.getBody();
	}

	public Boolean isMobileAvailableMoreThanThree(String mobile) {
		final String uri = LDAP_URL + "/isMobileAvailableMoreThanThree?mobile=" + mobile;
		return restTemplate.getForObject(uri, Boolean.class);
	}
	//

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

//	public List<String> findDomains(String bo) {
//		final String uri = LDAP_URL+"/findDomains?bo=" + bo;
//		return restTemplate.getForObject(uri, String.class);
//
//	}

	public List<String> findDomains(String bo) {
		String uri = LDAP_URL + "/findDomains?bo=" + bo;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
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

	// need to ask
	public Boolean uidEmailValidate(String uid) {
		final String uri = LDAP_URL + "/findByUid?uid=" + uid;
		if (restTemplate.getForObject(uri, Object.class) == null) {
			return false;
		}
		return true;
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
		ResponseEntity<Set<String>> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
				new ParameterizedTypeReference<Set<String>>() {
				});
		return response.getBody();
	}

	public Set<String> fetchCoordinators(OrganizationBean organizationDetails) {
		String uri = COORD_URL + "/fetchCoordinators";
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

	public UserForSearch findByMobile(String mobile) {
		final String uri = LDAP_URL + "/findByMobile?mobile=" + mobile;
		return restTemplate.getForObject(uri, UserForSearch.class);
	}

	public Boolean isEditable(String regNumber, String email) {
		final String uri = REPORTING_URL + "/isEditable?email=" + email + "&regNumber=" + regNumber;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	/*
	 * public Boolean createMailUsers(UserForCreate user, String po, String bo) {
	 * final String uri = LDAP_URL+"/createMailUsers?user=" + user +"&po="+ po
	 * +"&bo"+bo ; return restTemplate.getForObject(uri, Boolean.class); }
	 */

	/*
	 * public void sendObj(UserForCreate userForCreate, String bo, String parentBo)
	 * { final String uri = LDAP_URL + "/createMailUsers?bo=" + bo + "&parentBo=" +
	 * parentBo; ResponseEntity<String> temp = restTemplate.postForEntity(uri,
	 * userForCreate, String.class); System.out.println(temp.getStatusCodeValue() +
	 * "||" + temp.getBody()); }
	 */
	/*
	 * public Boolean createAppUsers(UserForCreateForAppUsers user, String po,
	 * String bo) { final String uri = LDAP_URL + "/createAppUsers?user=" + user +
	 * "&po=" + po + "&bo" + bo; return restTemplate.getForObject(uri,
	 * Boolean.class); }
	 */

	public User findBymailBean(String email) {
		final String uri = LDAP_URL + "/findByMail?mail=" + email;
		return restTemplate.getForObject(uri, User.class);
	}
	
	public Boolean createMailUsers(UserForCreate user, String po, String bo) {
		final String uri = LDAP_URL + "/createMailUsers?user=" + user + "&po=" + po + "&bo" + bo;
		return restTemplate.getForObject(uri, Boolean.class);
	}

	public Boolean createAppUsers(UserForCreateForAppUsers user, String po, String bo) {
		final String uri = LDAP_URL + "/createAppUsers?bo=" + bo + "&parentBo=" + po;
		return restTemplate.postForEntity(uri, user, Boolean.class).getBody();
		// return restTemplate.getForObject(uri, Boolean.class);
	}

	public void sendObj1(UserForCreateForAppUsers userForCreateForAppUsers, String bo, String parentBo) {
		final String uri = LDAP_URL + "/createAppUsers?bo=" + bo + "&parentBo=" + parentBo;
		ResponseEntity<String> temp = restTemplate.postForEntity(uri, userForCreateForAppUsers, String.class);
		System.out.println(temp.getStatusCodeValue() + "||" + temp.getBody());
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

	public List<String> fetchDomainsByCatAndMinAndDep(String empCategory, String ministry, String empDept) {
		String uri = COORD_URL + "/fetchDomainsByCatAndMinAndDep?empCategory=" + empCategory + "&ministry=" + ministry
				+ "&empDept=" + empDept;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	public List<String> fetchDomainsByCatAndMin(String empCategory, String ministry) {
		String uri = COORD_URL + "/fetchDomainsByCatAndMin?empCategory=" + empCategory + "&ministry=" + ministry;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	public List<String> fetchPos(String base) {
		String uri = LDAP_URL + "/fetchPos?base=" + base;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	public List<String> fetchBos(String base) {
		String uri = LDAP_URL + "/fetchBos?base=" + base;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	public List<String> fetchDomains(String bo) {
		String uri = LDAP_URL + "/findDomains?bo=" + bo;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	public Boolean isNicOutsourced(String mail) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		String urlToAuthenticate = LDAP_URL + "/isNicOutsourced";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlToAuthenticate).queryParam("mail", mail);
		HttpEntity<Boolean> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity,
				Boolean.class);
		return response.getBody();
	}

	public List<String> emailsAgainstMobile(String mobile) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		final String uri = LDAP_URL + "/fetchEmailsAgainstMobile?mobile=" + mobile;
		System.out.println("count the number of mobile=" + uri);
		// UriComponentsBuilder builder =
		// UriComponentsBuilder.fromHttpUrl(uri).queryParam("mobile", mobile);
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		System.out.println("count the number of mobile response=" + response);
		return response.getBody();
	}

	public String Mail(String mail, String uid, Set<String> allowedDomain) {
		String errorMsg = "";
		System.out.println("mail:::::::" + mail);
		if (mail.contains("@")) {
			String[] mle = mail.split("@");
			System.out.println("mleeeeeeeeeeeee" + mle);
			if (mle[0] != null) {
				System.out.println("mle000000000000" + mle[0] + "mle1" + mle[1]);
				String uidGenerated = mle[0];
				String domainGenerated = mle[1];
				uidGenerated = uidGenerated.toLowerCase().trim();
				domainGenerated = domainGenerated.toLowerCase().trim();
				System.out.println("DOMAIN Genrated :: " + domainGenerated);
				System.out.println("Allowed Domains ::: " + allowedDomain + " CHECK :: "
						+ !allowedDomain.contains(domainGenerated));
				try {
					if (!allowedDomain.contains(domainGenerated)) {
						errorMsg = "You are not allowed to create the ids for domain (@" + domainGenerated + ")";
					} else {
						try {
							if (domainGenerated.equals("@gov.in") || domainGenerated.equals("@nic.in")) {
								if (!uidGenerated.trim().equals(uid.trim())) {
									errorMsg = "If domain is gov.in then email address (before the @) should be equal to uid"; // Removed
																																// nic.in/
																																// from
																																// the
																																// errorMsg
								}
							}
						} catch (Exception e) {
							errorMsg = "Some error occurred.";
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				errorMsg = "Some error occurred.";
			}
		} else {
			errorMsg = "Mail is not in correct format.";
		}
		return errorMsg;
	}

	public String checkAvailableEmail(String mobile) {
		String errorMsg = "";
		Set<String> emailsAgainstMobile = new HashSet<String>();
		System.out.println("mobile:::::::" + mobile);
		HashMap<String, Object> values = new HashMap<String, Object>();

		List<String> ldapemailsAgainstMobile = emailsAgainstMobile(mobile);

		for (String x : ldapemailsAgainstMobile) {
			emailsAgainstMobile.add(x);
		}
		System.out.println("emailsAgainstMobile:::::::" + emailsAgainstMobile);

		if (emailsAgainstMobile.size() > 10) {
			System.out.println("emailsAgainstMobile:::::1::" + emailsAgainstMobile.size());
			errorMsg = "There are already 3 email addresses registered against your mobile number (" + mobile + ")";
		} else if (emailsAgainstMobile.size() == 10) {
			System.out.println("emailsAgainstMobile:::::2::");
			if (emailsAgainstMobile.contains("-admin")) {
				System.out.println("emailsAgainstMobile:::::3::");
				errorMsg = "There are already 3 email addresses registered against your mobile number (" + mobile + ")";
			} else {
				System.out.println("emailsAgainstMobile:::::4::");
				errorMsg = "There are already 3 email addresses registered against your mobile number (" + mobile + ")";
			}
		}
		return errorMsg;

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

	public List<String> fetchBoByMinistry(String empCategory, String ministry, String empDept) {
		String uri = COORD_URL + "/fetchBoByMinistry?empCategory=" + empCategory + "&ministry=" + ministry + "&empDept="
				+ empDept;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	public List<String> fetchBoByState(String empCategory, String state, String empDept) {
		String uri = COORD_URL + "/fetchBoByState?empCategory=" + empCategory + "&state=" + state + "&empDept="
				+ empDept;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	public List<String> fetchBoByOrg(String empCategory, String organization) {
		String uri = COORD_URL + "/fetchBoByOrg?empCategory=" + empCategory + "&organization=" + organization;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	// 1
	public List<String> fetchByEmploymentCategory() {
		String uri = COORD_URL + "/fetchByEmploymentCategory";
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	public List<String> fetchByCentralMinistry(String empCategory) {
		String uri = COORD_URL + "/fetchByCentralMinistry?empCategory=" + empCategory;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	public List<String> fetchByCentralDept(String ministry) {
		String uri = COORD_URL + "/fetchByCentralDept?ministry=" + ministry;
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

//end 3
	public List<String> fetchdistDomain() {
		String uri = COORD_URL + "/fetchdistDomain";
		ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

	/* AUTHOR : SACHIN MALIK, WORKING : GET DATA FROM LDAP USING UID. DATE : 20-09-2021. RETURN TYPE : User  */
	public User getDetailsForEditFromLdap(String uid)
			throws JSONException {
		final String userJsonData = LDAP_URL + "/findByUid?uid=" + uid;		
		return restTemplate.getForObject(userJsonData, User.class);
	}
	
	
	/* AUTHOR : SACHIN MALIK, WORKING : GET DATA FROM LDAP USING UID. DATE : 22-09-2021. RETURN TYPE : UserSearchBean */
	public UserSearchBean getDetailsFromLdap(String uid)
			throws JSONException {
		final String userJsonData = LDAP_URL + "/findByUid?uid=" + uid;		
		return restTemplate.getForObject(userJsonData, UserSearchBean.class);
	}
	
	/* AUTHOR : SACHIN MALIK, WORKING : GET DATA FROM LDAP USING UID FOR EDIT AND UPDATE DATE OF EXPIRY. DATE : 20-09-2021 */
	public Boolean editProfileInLdap(UserAttributes userAttribute)
			throws JSONException {
		String uri = LDAP_URL + "/updateThroughUid";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<UserAttributes> entity = new HttpEntity<>(userAttribute, headers);
  
		Map response =  restTemplate.postForObject(uri, entity, Map.class);
			    return (Boolean)response.get("response");
	}
	
	/* Author : Sachin Malik, Working : Update Date Of Expiry*/
	public String updateDateOfExpiry(String uid, String dateOfExpiry){
		final String uri = LDAP_URL + "/extendDateOfExpiry?uid=" + uid+"&dateOfExpiry="+dateOfExpiry;
		return restTemplate.getForObject(uri, String.class);
	}
	
	/* Author : Sachin Malik, Working : Get data from using E-Mail */
	public String findBymail(String email) {
		final String uri = LDAP_URL + "/findByMail?mail=" + email;
		return restTemplate.getForObject(uri, String.class);
	}
	
	/* Author : Sachin Malik, Working : Rest Password using Uid */
	public String resetPassword(String uid, String password){
		final String uri = LDAP_URL + "/resetPassword?uid=" + uid+"&password="+password;
		return restTemplate.getForObject(uri, String.class);
	}
	
	/* Author : Sachin Malik, Working : E-Mail Validate */
	public String validateEmail(String mail) {
		final String uri = LDAP_URL + "/validateEmail?mail=" + mail;
		return restTemplate.getForObject(uri, String.class);
	}
	
	/* Author : Sachin Malik, Working : Get data from ldap using E-Mail */
	public String updateThroughMail(UserAttributes userAttributes){
		final String uri = LDAP_URL + "/updateThroughMail";
		ResponseEntity<Boolean> temp = restTemplate.postForEntity(uri, userAttributes, Boolean.class);
		return temp.getBody().toString();
	}
	
	/* Author : Sachin Malik, Working : Delete user account */
	public Boolean deleteUserAccount(UserSearchBean userAttributes){
		final String uri = LDAP_URL + "/deletePartially";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<UserSearchBean> entity = new HttpEntity<>(userAttributes, headers);
		Boolean response =  restTemplate.postForObject(uri, entity, Boolean.class);
		System.out.println("LDAP API RESPONSE ::::: "+response);
			    return response;
	}
	
	/* Author : Sachin Malik, Working : Move To Retired BO */
	public String moveToRetiredBOAccount(String uid) {
		final String uri = LDAP_URL + "/moveToRetiredBo?uid=" + uid;
		return restTemplate.getForObject(uri, String.class);
	}
	
	/* Author : Sachin Malik, Working : Add Alias using Uid with alias*/
	public String AddAlias(String uid, String alias){
		final String uri = LDAP_URL + "/addAlias?uid=" + uid+"&alias="+alias;
		return restTemplate.getForObject(uri, String.class);
	}
	
	/* Author : Sachin Malik, Working : find Dn using Uid with alias*/
	public String findDn(String uid, String alias){
		final String uri = LDAP_URL + "/findDn?uid=" + uid+"&alias="+alias;
		return restTemplate.getForObject(uri, String.class);
	}
	
	/* Author : Sachin Malik, Working : Deactivate user account */
	public String deactivateAccount(String uid) {
		final String uri = LDAP_URL + "/inActivate?uid=" + uid;
		return restTemplate.getForObject(uri, String.class);
	}
	
	/* Author : Sachin Malik, Working : Exchange with Primary Equivalent */
	public String exchangePrimaryWithAlias(String uid, String exchangeEquivalent) {
		final String uri = LDAP_URL + "/swapPrimaryWithAlias?uid=" + uid + "&aliasToBeSwapped=" + exchangeEquivalent;
		return restTemplate.getForObject(uri, String.class);
	}
	
	/* Author : Sachin Malik, Working : Enable POP */
	public String enablePOP(String uid) {
		final String uri = LDAP_URL + "/disablePop?uid=" + uid;
		return restTemplate.getForObject(uri, String.class);
	}
	
	/* Author : Sachin Malik, Working : Disable POP */
	public String disablePOP(String uid) {
		final String uri = LDAP_URL + "/disablePop?uid=" + uid;
		return restTemplate.getForObject(uri, String.class);
	}
	
	/* Author : Sachin Malik, Working : Enable IMAP */
	public String enableIMAP(String uid) {
		final String uri = LDAP_URL + "/enableImap?uid=" + uid;
		return restTemplate.getForObject(uri, String.class);
	}
	
	/* Author : Sachin Malik, Working : Disable IMAP */
	public String disableIMAP(String uid) {
		final String uri = LDAP_URL + "/disableImap?uid=" + uid;
		return restTemplate.getForObject(uri, String.class);
	}
	
	
	//Validation's
	
	
	public String dorValidation(String dor, String dob) {
		String msg = "";
		if (dor.isEmpty()) {
			msg = "Please Enter Date of Retirement";
		} else if (!dor.matches("([0-9]{2})[-][0-9]{2}[-][0-9]{4}")) {
			msg = "Please select Date of Retirement in correct format";
		} else // dor = yearrt + "-" + monthrt + "-" + dayrt;
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
						// if ((years) > 65 || ((years) == 65) && (Integer.parseInt(month) >
						// Integer.parseInt(dob_month))) {
						// if ((years) > 66 || ((years) == 66) && (month > dob_month))
						if ((years) > 67 || ((years) == 67) && (month > dob_month)) // line modified by pr on 2ndaug18
						{
							msg = "year of retirement can not exceed 67 years from the DOB year"; // 66 -> 67 by pr on
																									// 2ndaug18
						} else {
							msg = "";
						}
						String pdate = format.format(date);
						java.util.Date date1 = format.parse(pdate);
						java.util.Date date2 = format.parse(dor);

						// int yr60 = date1.getYear() + 48 + 1900;
						int yr60 = date1.getYear() + 49 + 1900; // to add in the current year to make it 67 , modified
																// by pr on 2ndaug18

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
								msg = "year of retirement can not exceed 67 years from the DOB year"; // 66 -> 67 done
																										// by pr on
																										// 2ndaug18
							} else if (mr == mr1) {
								if (d2 < d1) {
									msg = "year of retirement can not exceed 67 years from the DOB year"; // 66 -> 67
																											// done by
																											// pr on
																											// 2ndaug18
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
	
}
