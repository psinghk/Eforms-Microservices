package in.nic.eForms.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.nic.eForms.entities.EmailDeActBase;
import in.nic.eForms.entities.UidCheck;
import in.nic.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.HodDetailsDto;
import in.nic.eForms.models.ManualUploadBean;
import in.nic.eForms.models.OrganizationBean;
import in.nic.eForms.models.OrganizationDto;
import in.nic.eForms.models.PreviewFormBean;
import in.nic.eForms.models.ProfileDto;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.models.UserForSearch;
import in.nic.eForms.repositories.EmailDeActBaseRepo;
import in.nic.eForms.repositories.EmailDeActEmpCoordRepo;
import in.nic.eForms.repositories.UidCheckRepo;
import in.nic.eForms.utils.Constants;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailDeActUserService {
	private final ResponseBean responseBean;
	private final EmailDeActBaseRepo emailDeActBaseRepo;
	private final EmailDeActEmpCoordRepo emailDeActEmpCoordRepo;
	private final UidCheckRepo uidCheckRepo;
	private final Util utilityService;
 
	 @Value("${fileBasePath}")
	 private String fileBasePath;
	
	@Autowired
	public EmailDeActUserService(EmailDeActBaseRepo emailDeActBaseRepo, UidCheckRepo uidCheckRepo,
			EmailDeActEmpCoordRepo nknSingleEmpCoordRepo, Util utilityService) {
		super();
		this.responseBean = new ResponseBean();
		this.emailDeActBaseRepo = emailDeActBaseRepo;
		this.uidCheckRepo = uidCheckRepo;
		this.utilityService = utilityService;
		this.emailDeActEmpCoordRepo = nknSingleEmpCoordRepo;

	}

	public Set<String> getDomain1(@RequestParam String empType, String email, @RequestParam String org,
			@RequestParam String min, @RequestParam String dep, @RequestParam String reqUserType) {
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		TreeSet<String> finaldomain = new TreeSet<>();

		if (reqUserType.toLowerCase().equals("other")) {
			if (org != null && !org.isEmpty() && min != null && !min.isEmpty()) {
				profile.setEmployment(org);
				if (org.toLowerCase().equals("central")) {
					profile.setDepartment(dep);
					profile.setMinistry(min);

					List<String> temp = utilityService.fetchDomainsByCatAndMinAndDep(org, min, dep);
					for (String string : temp) {
						if (string != null) {
							finaldomain.add(string);
							log.info(":::::1::::" + finaldomain);
							System.out.println("allowed domain:::" + finaldomain);
						}
					}

				}
				if (org.toLowerCase().equals("state")) {
					profile.setState(min);
					profile.setDepartment(dep);
					List<String> temp = utilityService.fetchDomainsByCatAndMinAndDep(org, min, dep);

					for (String string : temp) {
						if (!string.equals("null")) {
							finaldomain.add(string);
							System.out.println(":::::2::::" + finaldomain);
						}
					}
				}

				if (org.toLowerCase().equals("ut") || org.toLowerCase().equals("nkn") || org.toLowerCase().equals("psu")
						|| org.toLowerCase().equals("others") || org.toLowerCase().equals("project")) {
					// org = min;
					profile.setOrganization(org);
					List<String> temp = utilityService.fetchDomainsByCatAndMin(org, min);
					System.out.println(":::::3::::" + org + ":::::::::::" + min);

					for (String string : temp) {
						if (!string.equals("null")) {
							finaldomain.add(string);
							log.info(":::::3::::" + finaldomain);
							System.out.println("allowed domain:::" + finaldomain);
						}
					}

				}
			}
		}

		else if (reqUserType.toLowerCase().equals("self")) {
			System.out.println("inside self");
			try {
				if (profile.getEmployment().equalsIgnoreCase("central")) {
					if (empType.equals("emp_contract")) {
						finaldomain.add("supportgov.in");
					} else if (empType.equals("consultant")) {
						finaldomain.add("govcontractor.in");
					
					}

					else if (profile.getEmployment().equalsIgnoreCase("central")
							|| profile.getEmployment().equalsIgnoreCase("ut")) {
						log.info(":::::1::::" + profile.getEmployment() + ":::::::::::" + profile.getMinistry()
								+ "::::::::::" + profile.getDepartment());
						List<String> temp = utilityService.fetchDomainsByCatAndMinAndDep(profile.getEmployment(),
								profile.getMinistry(), profile.getDepartment());
						for (String string : temp) {
							if (string != null) {
								finaldomain.add(string);
								log.info(":::::1::::" + finaldomain);
								System.out.println("allowed domain:::" + finaldomain);
							}
						}
					} else if (profile.getEmployment().equalsIgnoreCase("state")) {
						log.info(":::::2::::" + profile.getEmployment() + ":::::::::::" + profile.getState()
								+ "::::::::::" + profile.getDepartment());
						List<String> temp = utilityService.fetchDomainsByCatAndMinAndDep(profile.getEmployment(),
								profile.getState(), profile.getDepartment());

						for (String string : temp) {
							if (!string.equals("null")) {
								finaldomain.add(string);
								log.info(":::::2::::" + finaldomain);
								System.out.println("allowed domain:::" + finaldomain);
							}
						}
					} else {
						List<String> temp = utilityService.fetchDomainsByCatAndMinAndDep(profile.getEmployment(),
								profile.getState(), profile.getDepartment());

						for (String string : temp) {
							if (!string.equals("null")) {
								finaldomain.add(string);
								System.out.println(":::::2::::" + finaldomain);
								System.out.println("allowed domain:::" + finaldomain);
							}
						}
					}
				

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		} else {

			return finaldomain;
		}
		return finaldomain;
	}

	public Set<String> getnknDomain() {
		Set<String> domains = new HashSet<>();
		try {
			domains = emailDeActEmpCoordRepo.fetchdistDomain();
		} catch (Exception e) {
			log.info(e.getMessage());
		} finally {
		}
		return domains;
	}

	public EmailDeActBase fetchDetails(String regNo) {
		return emailDeActBaseRepo.findByRegistrationNo(regNo);
	}

	@Transactional
	public EmailDeActBase insert(EmailDeActBase ldapBase) {
		if (ldapBase != null) {
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String pdate = dateFormat.format(date);
			String oldRegNumber = emailDeActBaseRepo.findLatestRegistrationNo();
			String newRegNumber = "EMAILDEACTIVATE-FORM" + pdate;
			if (oldRegNumber == null || oldRegNumber.isEmpty()) {
				newRegNumber += "0001";
			} else {
				String lastst = oldRegNumber.substring(30, oldRegNumber.length());
				int last = Integer.parseInt(lastst);
				int newrefno = last + 1;
				int len = Integer.toString(newrefno).length();
				if (len == 1) {
					newRegNumber += "000" + newrefno;
				} else if (len == 2) {
					newRegNumber += "00" + newrefno;
				} else if (len == 3) {
					newRegNumber += "0" + newrefno;
				}
			}
			ldapBase.setRegistrationNo(newRegNumber);
			ldapBase.setSupportActionTaken("p");
			ldapBase.setEmailSent("N");
			ldapBase.setEmailSentTo("N");
			ldapBase.setSmsSent("N");
			ldapBase.setSmsSentTo("N");
			return emailDeActBaseRepo.save(ldapBase);
		}
		return null;
	}

	public EmailDeActBase preview(String regNo) {
		return emailDeActBaseRepo.findByRegistrationNo(regNo);
	}
	public boolean updatePreviewDetails(String regNumber, PreviewFormBean previewFormBean) {
		EmailDeActBase ldapBase = emailDeActBaseRepo.findByRegistrationNo(regNumber);
		if (ldapBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, ldapBase);
				LocalDateTime currentTime = LocalDateTime.now();

				EmailDeActBase singleUpdated = emailDeActBaseRepo.save(ldapBase);
				if (singleUpdated.getId() > 0) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status, finalAuditTrack);
	}

	public String utilityService() {
		return null;

	}

	public ResponseBean submitRequest(PreviewFormBean previewFormBean, String ip, String email, String submissionType
			 ) {
		String formType = "emaildeactivate";
		log.info("on submit");
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		responseBean.setRequestType("Submission of request");
		Map<String, Object> map = validateRequest(previewFormBean, email);
		log.info("log:info:Map Size:::: " + map.size());
		log.info("Map Size:::: " + map.size());
		log.info("Map:::: " + map);
		if (map.size() == 0) {
			responseBean.setErrors(null);
			log.info("responseBean:::: " + responseBean);
			if (profile != null) {

				String hmapp = utilityService.checkAvailableEmail(profile.getMobile());
				System.out.println("hmapp:::available:::"+hmapp);
				if (hmapp.equals("")) {
					log.info("profile:::: " + profile);

					status = utilityService.initializeStatusTable(ip, email, formType, previewFormBean.getRemarks(),
							profile.getMobile(), profile.getName(), "user");

					log.info("status:::: " + status);

					finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType,
							previewFormBean.getRemarks(), profile.getMobile(), profile.getName(), "user", "");

					log.info("finalAuditTrack:::: " + finalAuditTrack);
					ModelMapper modelMapper = new ModelMapper();

					if (previewFormBean.getEmployment().equalsIgnoreCase("state")
							&& previewFormBean.getState().equalsIgnoreCase("Himachal Pradesh")) {
						log.info("Himachal Pradesh:::: ");
						List<String> himachalCoords = utilityService
								.fetchHimachalCoords(previewFormBean.getDepartment());
						if (himachalCoords != null && himachalCoords.size() > 0) {
							String coordEmail = himachalCoords.get(0);
							if (utilityService.isGovEmployee(coordEmail)) {
								HodDetailsDto roDetails = utilityService.getHodValues(coordEmail);
								log.info("roDetails:::: " + roDetails);
								profile.setHodEmail(coordEmail);
								profile.setHodMobile(roDetails.getMobile());
								profile.setHodName(roDetails.getFirstName());
								profile.setHodDesignation(roDetails.getDesignation());
								profile.setHodTelephone(roDetails.getTelephoneNumber());
							}
						}
					}

					EmailDeActBase emailDeActBase = modelMapper.map(profile, EmailDeActBase.class);
					log.info("EmailDeActBase:1::: " + emailDeActBase);
					emailDeActBase.setPdfPath(submissionType);
					log.info("previewFormBean:1::: " + previewFormBean);
					
					BeanUtils.copyProperties(previewFormBean, emailDeActBase);
					LocalDateTime currentTime = LocalDateTime.now();
					emailDeActBase.setDatetime(currentTime);
					emailDeActBase.setUserIp(ip);

					for (int i = 0; i < 4; i++) {
						emailDeActBase = insert(emailDeActBase);
						log.info("EmailDeaAct " + emailDeActBase);
						if (emailDeActBase.getId() > 0) {
							log.info("EmailDeActBase:::: " + emailDeActBase.getId());
							break;
						}
					}

					if (emailDeActBase.getId() > 0) {
						if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
							log.info("online:::: ");
							status.setRegistrationNo(emailDeActBase.getRegistrationNo());
							status.setRecipientType(Constants.STATUS_CA_TYPE);
							status.setStatus(Constants.STATUS_CA_PENDING);
							status.setRecipient(profile.getHodEmail());

							finalAuditTrack.setRegistrationNo(emailDeActBase.getRegistrationNo());
							finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
							finalAuditTrack.setToEmail(profile.getHodEmail());
							finalAuditTrack.setAppUserType(submissionType);

							if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
								log.info("{} submitted successfully.", emailDeActBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer("
												+ profile.getHodEmail() + ")");
								responseBean.setRegNumber(emailDeActBase.getRegistrationNo());
							} else {
								log.debug("Something went wrong. Please try again after sometime.");
								responseBean.setStatus("Something went wrong. Please try again after sometime.");
								responseBean.setRegNumber("");
							}
						} else {

							status.setRegistrationNo(emailDeActBase.getRegistrationNo());
							status.setRecipientType(Constants.STATUS_USER_TYPE);
							status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
							status.setRecipient(email);

							finalAuditTrack.setRegistrationNo(emailDeActBase.getRegistrationNo());
							finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
							finalAuditTrack.setToEmail(email);
							finalAuditTrack.setAppUserType("manual");

							if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
								log.info("{} submitted successfully.", emailDeActBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
								responseBean.setRegNumber(emailDeActBase.getRegistrationNo());
							} else {
								log.debug("Something went wrong. Please try again after sometime.");
								responseBean.setStatus("Something went wrong. Please try again after sometime.");
								responseBean.setRegNumber("");
							}
						}
					} else {
						log.debug("Something went wrong. Please try again after sometime.");
						responseBean.setStatus("Something went wrong. Please try again after sometime.");
						responseBean.setRegNumber("");
					}

				} else {
					if (!(hmapp.equals(""))) {
						responseBean.setStatus(hmapp);
					}
					responseBean.setRegNumber("");
					responseBean.setStatus("Application could not be submitted.");
				}

			} else {
				log.warn(
						"Hey, {}, We do not have your profile in eForms. Please go to profile section and make your profile first");
				responseBean.setStatus(
						"We do not have your profile in eForms. Please go to profile section and make your profile first");
				responseBean.setRegNumber("");
			}
		} else {
			responseBean.setErrors(map);
			responseBean.setRegNumber("");
			responseBean.setStatus("Application could not be submitted.");
		}
		return responseBean;
	}

	public String allLdapValues(String preferredEmail1, String email) {
		// ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		String[] words = preferredEmail1.split("\\s+");
		if (utilityService.allLdapValues(preferredEmail1) == null) {
			String[] splitString = preferredEmail1.split("@");
			System.out.println("splitted:::" + splitString);
			String uidPreferredEmail1 = splitString[0];
			String domainGenerated = splitString[1];

			if (Character.isDigit(preferredEmail1.charAt(0))) {
				return "Mail cannot start with a numeric value.";
			} else if (preferredEmail1.startsWith("-") || preferredEmail1.startsWith(".")) {
				return "Mail cannot starts with dot[.] and hyphen[-].";
			} else if (preferredEmail1.contains("_")) {
				return "Mail cannot contain underscore[_].";
			} else if (preferredEmail1.endsWith(".") || preferredEmail1.endsWith("-") || preferredEmail1.startsWith(".")
					|| preferredEmail1.startsWith("-")) {
				return "Mail can not start or end with dot[.] and hyphen[-]. ";
			} else if (preferredEmail1.contains("..") || preferredEmail1.contains("--")) {
				return "Mail can not contain continuous dot[.] or hyphen[-].";
			} else if (!preferredEmail1.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) {
				return "Mail is not in correct format.";
			} else if (words.length > 1) {
				return "Mail cannot contain whitespaces.";
			} else if (isReservedKeyWord(uidPreferredEmail1)) {
				return "This mail address " + preferredEmail1 + "  not allowed.";
			}

			return preferredEmail1;
		}

		else {
			return "Login ID (The part before the '@') already exists.";
		}
	}

	public String allLdapValues1(String preferredEmail1) {
		if (utilityService.allLdapValues(preferredEmail1) == null) {
			return preferredEmail1;
		} else {
			return "Login ID (The part before the '@') already exists.";
		}
	}

	public boolean isReservedKeyWord(String uid) {
		boolean flag = false;
		Optional<UidCheck> en = uidCheckRepo.findById(uid);
		return en.isPresent();
	}
	
  public Map<String, Object> validate(String preferredEmail) 
      {		
	 log.info("validate the request");
	Map<String, Object> error = new HashMap<>();
	
	if(!preferredEmail.isEmpty()) {
		
		UserForSearch CompleteLdapData = featchStatusFromLdap(preferredEmail);
	    if(CompleteLdapData==null) {
	    	error.put("emailError", "Please enter Government email address only");
	    }
	    else {
		String userStatus = CompleteLdapData.getUserInetStatus();
		String mailStatus =CompleteLdapData.getUserMailStatus(); 	
		System.out.println("userStatus :"+userStatus);
		
		if(!userStatus.equalsIgnoreCase("active") && !mailStatus.equalsIgnoreCase("active") ) {
			log.debug("Email is not for DeActivation:");
			error.put("emailError", "Please Enter Active email for DeActivation: ");
		}
	    }
	}
	else {
		error.put("emailError", "Please Enter Email For DeActivation");
	}

	return error;
}

//	
//	public Map<String, Object> validateRequest(PreviewFormBean previewFormBean, String email) {
//		Map<String, Object> map = new HashMap<>();
//		Map<String, Object> finalmap = new HashMap<>();
//		OrganizationDto organizationDetails = new OrganizationDto();
//		
//		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
//			String preferredEmail1 = previewFormBean.getPreferredEmail1();
//	//	Set<String> finaldomain = getDomain(empType, email);
//
//		if (!previewFormBean.getTnc()) {
//			log.debug("Terms and condition is not selected.");
//			map.put("tncError", "Please select Terms and Condition to proceed.");
//		}
//		
//		 UserForSearch CompleteLdapData = featchStatusFromLdap(preferredEmail1);
//			String userStatus = CompleteLdapData.getUserInetStatus();
//			String mailStatus =CompleteLdapData.getUserMailStatus(); 
//
//			if(!userStatus.equalsIgnoreCase("active") && !mailStatus.equalsIgnoreCase("active") ) {
//				log.debug("Email is not for DeActivation:");
//				map.put("emailStatusError", "Please Enter Active email for DeActivation: ");
//			}
//			
//		BeanUtils.copyProperties(previewFormBean, organizationDetails);
//		String organizationValidationResponse = utilityService.validateOrganization(organizationDetails);
//		ObjectMapper mapper = new ObjectMapper();
//		ErrorResponseForOrganizationValidationDto orgError = null;
//
//		if (organizationValidationResponse != null && !organizationValidationResponse.isEmpty()) {
//			log.debug("Errors in Organization");
//			try {
//				orgError = mapper.readValue(organizationValidationResponse,
//						ErrorResponseForOrganizationValidationDto.class);
//			} catch (JsonProcessingException e) {
//				e.printStackTrace();
//			}
//			if (orgError != null)
//				map.put("orgError", orgError);
//		}
//		if (map.size() > 1) {
//			finalmap.put("errors", map);
//		}
//		return map;
//	}
  
  public Map<String, Object> validateRequest(PreviewFormBean previewFormBean, String email) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> finalmap = new HashMap<>();
		OrganizationDto organizationDetails = new OrganizationDto();
		
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		String preferredEmail = previewFormBean.getPreferredEmail();

		if (!previewFormBean.getTnc()) {
			log.debug("Terms and condition is not selected.");
			map.put("tncError", "Please select Terms and Condition to proceed.");
		}
		
		if(!previewFormBean.getPreferredEmail().isEmpty()) {
			
			UserForSearch CompleteLdapData = featchStatusFromLdap(previewFormBean.getPreferredEmail());
		    if(CompleteLdapData==null) {
		    	map.put("emailError", "Please enter Government email address only");
		    }
		    else {
			String userStatus = CompleteLdapData.getUserInetStatus();
			String mailStatus =CompleteLdapData.getUserMailStatus(); 	
			System.out.println("userStatus :"+userStatus);
			
			if(!userStatus.equalsIgnoreCase("active") && !mailStatus.equalsIgnoreCase("active") ) {
				log.debug("Email is not for DeActivation:");
				map.put("emailError", "Please Enter active email for DeActivation: ");
			}
		    }
		}
		else {
			map.put("emailError", "Please Enter Email For DeActivation");
		}
		
		BeanUtils.copyProperties(previewFormBean, organizationDetails);
		String organizationValidationResponse = utilityService.validateOrganization(organizationDetails);
		ObjectMapper mapper = new ObjectMapper();
		ErrorResponseForOrganizationValidationDto orgError = null;

		if (organizationValidationResponse != null && !organizationValidationResponse.isEmpty()) {
			log.debug("Errors in Organization");
			try {
				orgError = mapper.readValue(organizationValidationResponse,
						ErrorResponseForOrganizationValidationDto.class);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			if (orgError != null)
				map.put("orgError", orgError);
		}
		if (map.size() > 1) {
			finalmap.put("errors", map);
		}
		return map;
	}
	
	public List<String> fetchByEmploymentCategory() {
		List<String> emailCoords = null;
		List<String> emailCoordinators = utilityService.fetchByEmploymentCategory();
		if (emailCoordinators != null && emailCoordinators.size() > 0) {
			emailCoords = new ArrayList<>();
			for (String temp : emailCoordinators) {
				emailCoords.add(temp);
			}
		}
		System.out.println("organizationCategory:::::" + emailCoordinators);
		return emailCoords;
	}

	public List<String> fetchByCentralMinistry(String organizationCategory) {
		List<String> emailCoords = null;
		List<String> emailCoordinators = utilityService.fetchByCentralMinistry(organizationCategory);
		if (emailCoordinators != null && emailCoordinators.size() > 0) {
			emailCoords = new ArrayList<>();
			for (String temp : emailCoordinators) {
				emailCoords.add(temp);
			}
		}
		System.out.println("ministryOrganization:::::" + emailCoordinators);
		return emailCoords;
	}

	public List<String> fetchByCentralDept(String ministryOrganization) {
		List<String> emailCoords = null;
		List<String> emailCoordinators = utilityService.fetchByCentralDept(ministryOrganization);
		if (emailCoordinators != null && emailCoordinators.size() > 0) {
			emailCoords = new ArrayList<>();
			for (String temp : emailCoordinators) {
				emailCoords.add(temp);
			}
		}
		System.out.println("departmentDivisionDomain:::::" + emailCoordinators);
		return emailCoords;
	}

	public ResponseBean approve(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Approval of request by user as it was submitted manually");
		String formType = "single";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		EmailDeActBase emailDeActBase = fetchDetails(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, emailDeActBase.getMobile(),
				emailDeActBase.getName(), "user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType(Constants.STATUS_CA_TYPE);
		status.setStatus(Constants.STATUS_CA_PENDING);
		status.setRecipient(emailDeActBase.getHodEmail());

		finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
		finalAuditTrack.setToEmail(emailDeActBase.getHodEmail());
		
		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("{} submitted successfully.", regNumber);
			responseBean
					.setStatus("Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
							+ emailDeActBase.getHodEmail() + ")");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber("");
		}
		return responseBean;
	}

	public ResponseBean reject(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Cancellation of request by user.");
		log.info("EmailDeAct reject request called by {}",email);
		String formType = "emaildeactivate";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		EmailDeActBase emailDeActBase = fetchDetails(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, emailDeActBase.getMobile(),
				emailDeActBase.getName(), "user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_USER_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_USER_REJECTED);
		finalAuditTrack.setToEmail("");

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("{} cancelled successfully.", regNumber);
			responseBean.setStatus(regNumber + " cancelled successfully");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber("");
		}

		return responseBean;
	}
	public ResponseBean manualupload(@ModelAttribute("manualUploadBean") ManualUploadBean manualUploadBean,HttpServletRequest request, ResponseBean responseBean) throws IOException {
		
		manualUploadBean.setEmail(request.getParameter("email"));
		manualUploadBean.setClientIp(request.getParameter("clientIp"));
		
		responseBean.setRequestType("Forwarding of request by user");
		log.info("EmailDeAct manual upload request");
		String formType = "emaildeactivate";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		EmailDeActBase emailDeActBase = preview(manualUploadBean.getRegNumber());
		String[] contenttype = manualUploadBean.getInfile().getContentType().split("/");
		String ext = contenttype[1];
		String outputfile = new StringBuilder().append(manualUploadBean.getRegNumber()).append("_")
				.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
				.append(".").append(ext).toString();
		
		emailDeActBase.setPdfPath(fileBasePath+outputfile);
		byte[] bytes = manualUploadBean.getInfile().getBytes();
		Path path = Paths.get(fileBasePath + outputfile);
		boolean stat = emailDeActbase(emailDeActBase);
		if(stat) {
			
			Files.write(path, bytes);
		}
		else {
			
			responseBean.setStatus("File failed to upload");
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
   		return responseBean;
		}
		String dn = utilityService.findDn(emailDeActBase.getEmail());
		String roDn = utilityService.findDn(manualUploadBean.getEmail());
		List<String> aliases = utilityService.aliases(manualUploadBean.getEmail());
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";
		
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), emailDeActBase.getHodMobile(),
				emailDeActBase.getHodName(),"usermanual" ,manualUploadBean.getRegNumber());
		status = utilityService.initializeStatusTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), emailDeActBase.getHodMobile(), emailDeActBase.getHodName(),"user");
		
         if (emailDeActBase.getEmployment().equalsIgnoreCase("State") && emailDeActBase.getState().equalsIgnoreCase("punjab")
				&& emailDeActBase.getPostingState().equalsIgnoreCase("punjab")) {
        		toWhom = "ro";
    			daEmail = emailDeActBase.getHodEmail();
    			recipientType = Constants.STATUS_CA_TYPE;
    			nextStatus = Constants.STATUS_CA_PENDING;	
		} else if (utilityService.isNicEmployee(manualUploadBean.getEmail()) && (emailDeActBase.getPostingState().equalsIgnoreCase("delhi")
				&& emailDeActBase.getEmployment().equalsIgnoreCase("central")
				&& (dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))) {
			toWhom = "Admin";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} else if (utilityService.isNicEmployee(emailDeActBase.getEmail()) || utilityService.isNicEmployee(manualUploadBean.getEmail())) {
			toWhom = "ro";
			daEmail = emailDeActBase.getHodEmail();
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;	
			
		} else if (dn!=null && ((dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))
				&& (roDn.contains("nic support outsourced") || roDn.contains("nationalknowledgenetwork"))) {
			toWhom = "ro";
			daEmail = emailDeActBase.getHodEmail();
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;
			
		} else if (emailDeActBase.getEmployment().equalsIgnoreCase("Others")
				&& emailDeActBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& emailDeActBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& emailDeActBase.getCity().equalsIgnoreCase("pune") && (emailDeActBase.getAddress().toLowerCase().contains("ndc")
						|| emailDeActBase.getAddress().toLowerCase().contains("national data center"))) {
			toWhom = "Coordinator";
			daEmail = utilityService.fetchNdcPuneCoord();

			if (aliases.contains(daEmail)) {
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
			} else {
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			}
		}  else {
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(emailDeActBase, OrganizationBean.class);
				toWhom = "Reporting officer";
				daEmail = emailDeActBase.getHodEmail();
				recipientType = Constants.STATUS_CA_TYPE;
				nextStatus = Constants.STATUS_CA_PENDING;
		}
		status.setRegistrationNo(manualUploadBean.getRegNumber());
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("Application (" + manualUploadBean.getRegNumber() + ")  Forwarded Successfully to " + toWhom + "(" + daEmail
					+ ")");
			responseBean.setStatus("Application (" + manualUploadBean.getRegNumber() + ") Forwarded Successfully to " + toWhom
					+ "(" + daEmail + ")");
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
		} else {
			log.debug(Constants.ERROR_MESSAGE);
			responseBean.setStatus(Constants.ERROR_MESSAGE);
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
		}
		return responseBean;
	}
	public boolean emailDeActbase(EmailDeActBase emailDeActBase) {
		EmailDeActBase details = emailDeActBaseRepo.save(emailDeActBase);
		if (details.getId() > 0) {
			return true;
		} else {
			return false;
		}
	}
	public UserForSearch featchStatusFromLdap(String mail) {
		
		return utilityService.findByMail(mail);    
	}
	
	public Map<String, Object> checkExistPreferredEmail1(String preferredEmail1){
		System.out.println("email for DeActivation " + preferredEmail1);
		Map<String, Object> map = new HashMap<>(); 
		
		    UserForSearch CompleteLdapData = featchStatusFromLdap(preferredEmail1);
		    
		    if(CompleteLdapData==null) {
		    	map.put("emailError", "Please enter Government email address only");
		    }
		    else {
			String userStatus = CompleteLdapData.getUserInetStatus();
			String mailStatus =CompleteLdapData.getUserMailStatus(); 	
			System.out.println("userStatus :"+userStatus);
			
			 if(userStatus==null) {
			    	map.put("emailError", "Please enter Government email address only");
			    }
			 
			 else if(!userStatus.equalsIgnoreCase("active") && !mailStatus.equalsIgnoreCase("active") ) {
				log.debug("Email is not for DeActivation:");
				map.put("emailError", "Please Enter active email for DeActivation");
			 }
		    }
			return map;
	}
}
