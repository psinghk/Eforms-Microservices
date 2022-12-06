package in.nic.eForms.services;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.nic.eForms.entities.EmailActBase;
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
import in.nic.eForms.models.ResponseData;
import in.nic.eForms.models.Status;
import in.nic.eForms.models.UserForSearch;
import in.nic.eForms.models.ValidateFormBean;
import in.nic.eForms.repositories.EmailActBaseRepo;
import in.nic.eForms.repositories.EmailActEmpCoordRepo;
import in.nic.eForms.repositories.UidCheckRepo;
import in.nic.eForms.utils.Constants;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailActUserService {
	private final ResponseBean responseBean;
	private final EmailActBaseRepo emailActBaseRepo;
	private final EmailActEmpCoordRepo emailActEmpCoordRepo;
	private final UidCheckRepo uidCheckRepo;
	private final Util utilityService;
 
	 @Value("${fileBasePath}")
	 private String fileBasePath;
	
	@Autowired
	public EmailActUserService(EmailActBaseRepo emailActBaseRepo, UidCheckRepo uidCheckRepo,
			EmailActEmpCoordRepo nknSingleEmpCoordRepo, Util utilityService) {
		super();
		this.responseBean = new ResponseBean();
		this.emailActBaseRepo = emailActBaseRepo;
		this.uidCheckRepo = uidCheckRepo;
		this.utilityService = utilityService;
		this.emailActEmpCoordRepo = nknSingleEmpCoordRepo;

	}
	
	public Set<String> getDomain(@RequestParam String empType, String email) {
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		TreeSet<String> finaldomain = new TreeSet<>();
		
		try {
			if (profile.getEmployment().equalsIgnoreCase("central") ) {
				if (empType.equals("emp_contract")) {
					finaldomain.add("supportgov.in");
				} else if (empType.equals("consultant")) {
					finaldomain.add("govcontractor.in");
					
			} 
			
			else if (profile.getEmployment().equalsIgnoreCase("central") || profile.getEmployment().equalsIgnoreCase("ut")) {
				System.out.println(":::::1::::" + profile.getEmployment() + ":::::::::::" + profile.getMinistry()
						+ "::::::::::" + profile.getDepartment());
				List<String> temp = utilityService.fetchDomainsByCatAndMinAndDep(profile.getEmployment(),
						profile.getMinistry(), profile.getDepartment());
				for (String string : temp) {
					if (string != null) {
						finaldomain.add(string);
						System.out.println(":::::1::::" + finaldomain);
					}
				}
			} else if (profile.getEmployment().equalsIgnoreCase("state")) {
				System.out.println(":::::2::::" + profile.getEmployment() + ":::::::::::" + profile.getState()
						+ "::::::::::" + profile.getDepartment());
				List<String> temp = utilityService.fetchDomainsByCatAndMinAndDep(profile.getEmployment(),
						profile.getState(), profile.getDepartment());

				for (String string : temp) {
					if (!string.equals("null")) {
						finaldomain.add(string);
						System.out.println(":::::2::::" + finaldomain);
					}
				}
			} else{
				List<String> temp = utilityService.fetchDomainsByCatAndMin(profile.getEmployment(),
						profile.getMinistry());
				System.out.println(":::::3::::" + profile.getEmployment() + ":::::::::::" + profile.getOrganization());
				for (String string : temp) {
					if (!string.equals("null")) {
						finaldomain.add(string);
						System.out.println(":::::3::::" + finaldomain);
					}
				}
			}

		}
	

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return finaldomain;
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
			domains = emailActEmpCoordRepo.fetchdistDomain();
		} catch (Exception e) {
			log.info(e.getMessage());
		} finally {
		}
		return domains;
	}

	public EmailActBase fetchDetails(String regNo) {
		return emailActBaseRepo.findByRegistrationNo(regNo);
	}

	@Transactional
	public EmailActBase insert(EmailActBase ldapBase) {
		if (ldapBase != null) {
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String pdate = dateFormat.format(date);
			String oldRegNumber = emailActBaseRepo.findLatestRegistrationNo();
			String newRegNumber = "EMAILACTIVATE-FORM" + pdate;
			if (oldRegNumber == null || oldRegNumber.isEmpty()) {
				newRegNumber += "0001";
			} else {
				String lastst = oldRegNumber.substring(26, oldRegNumber.length());
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
			return emailActBaseRepo.save(ldapBase);
		}
		return null;
	}

	public EmailActBase preview(String regNo) {
		return emailActBaseRepo.findByRegistrationNo(regNo);
	}
	public boolean updatePreviewDetails(String regNumber, PreviewFormBean previewFormBean) {
		EmailActBase ldapBase = emailActBaseRepo.findByRegistrationNo(regNumber);
		if (ldapBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, ldapBase);
				LocalDateTime currentTime = LocalDateTime.now();

				EmailActBase singleUpdated = emailActBaseRepo.save(ldapBase);
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
		String formType = "emailactivate";
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

					EmailActBase emailActBase = modelMapper.map(profile, EmailActBase.class);
					log.info("EmailActBase:1::: " + emailActBase);
					emailActBase.setPdfPath(submissionType);
					log.info("previewFormBean:1::: " + previewFormBean);
					
					// for insert workOrder in base table:
					if (previewFormBean.getWorkrOrder() != null) {
						System.out.println("FILEEEe:::::::::::" + previewFormBean.getWorkrOrder().getOriginalFilename());
						emailActBase.setWorkrOrder(previewFormBean.getWorkrOrder().getOriginalFilename());
					
					}

					
					BeanUtils.copyProperties(previewFormBean, emailActBase);
					LocalDateTime currentTime = LocalDateTime.now();
					emailActBase.setDatetime(currentTime);
					emailActBase.setUserIp(ip);

					for (int i = 0; i < 4; i++) {
						emailActBase = insert(emailActBase);
						log.info("EmailActBase:2::: " + emailActBase);
						if (emailActBase.getId() > 0) {
							log.info("EmailActBase:::: " + emailActBase.getId());
							break;
						}
					}

					if (emailActBase.getId() > 0) {
						if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
							log.info("online:::: ");
							status.setRegistrationNo(emailActBase.getRegistrationNo());
							status.setRecipientType(Constants.STATUS_CA_TYPE);
							status.setStatus(Constants.STATUS_CA_PENDING);
							status.setRecipient(profile.getHodEmail());

							finalAuditTrack.setRegistrationNo(emailActBase.getRegistrationNo());
							finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
							finalAuditTrack.setToEmail(profile.getHodEmail());
							finalAuditTrack.setAppUserType(submissionType);
							
							if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
								log.info("{} submitted successfully.", emailActBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer("
												+ profile.getHodEmail() + ")");
								responseBean.setRegNumber(emailActBase.getRegistrationNo());
							} else {
								log.debug("Something went wrong. Please try again after sometime.");
								responseBean.setStatus("Something went wrong. Please try again after sometime.");
								responseBean.setRegNumber("");
							}
						} else {

//				

							status.setRegistrationNo(emailActBase.getRegistrationNo());
							status.setRecipientType(Constants.STATUS_USER_TYPE);
							status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
							status.setRecipient(email);

							finalAuditTrack.setRegistrationNo(emailActBase.getRegistrationNo());
							finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
							finalAuditTrack.setToEmail(email);

							if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
								log.info("{} submitted successfully.", emailActBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
								responseBean.setRegNumber(emailActBase.getRegistrationNo());
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
	public Map<Object, Object> validate(@Valid ValidateFormBean validateFormBean) 
	    {		
		log.info("validate the request");
		Map<Object, Object> error = new HashMap<>();
		
		if(!validateFormBean.getPreferredEmail().isEmpty()) {
			
			UserForSearch CompleteLdapData = featchStatusFromLdap(validateFormBean.getPreferredEmail());
		    if(CompleteLdapData==null) {
		    	error.put("emailError", "Please enter Government email address only");
		    }
		    else {
			String userStatus = CompleteLdapData.getUserInetStatus();
			String mailStatus =CompleteLdapData.getUserMailStatus(); 	
			System.out.println("userStatus :"+userStatus);
			
			if(!userStatus.equalsIgnoreCase("inactive") && !mailStatus.equalsIgnoreCase("inactive") ) {
				log.debug("Email is not for Activation:");
				error.put("emailError", "Please Enter Inactive email for Activation: ");
			}
		    }
		}
		else {
			error.put("emailError", "Please Enter Email For Activation");
		}
		
		
		if (validateFormBean.getDor().isEmpty()) {
			error.put("dorError", "Please Enter Date of retirement:");
		}
		if (validateFormBean.getEmpType().isEmpty()) {
			error.put("empTypeError", "Please Enter EmpType:");
		}
		//MultipartFile aa = validateFormBean.getWorkrOrder();
		
		if (!validateFormBean.getEmpType().contains("emp_regular") && validateFormBean.getWorkrOrder() == null) {
			
			error.put("workOrderFileError", "Please upload a workorder file");
			log.debug("need to upload a workorder file");
		}
		
		if(!validateFormBean.getEmpType().contains("emp_regular")) {
			
		if (!(validateFormBean.getWorkrOrder() == null) && !validateFormBean.getWorkrOrder().getOriginalFilename().contains(".pdf")) {
			error.put("fileError", "Please upload pdf file only");
		}
		if (!(validateFormBean.getWorkrOrder() == null) && !validateFormBean.getWorkrOrder().getOriginalFilename().contains(".pdf") && validateFormBean.getWorkrOrder().getSize() <= 1048927 ) {
			error.put("fileError", "Please upload less than 1 MB pdf file only");
		}
		if (validateFormBean.getWorkrOrder().getSize() >= 1048927 ) {
			error.put("fileError", "Please upload less than 1 MB pdf file only");
		}
		
		}
	
		return error;
	}
	
	public Map<String, Object> validateRequest(PreviewFormBean previewFormBean, String email) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> finalmap = new HashMap<>();
		OrganizationDto organizationDetails = new OrganizationDto();
		
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		String empType = previewFormBean.getEmpType();
		String preferredEmail = previewFormBean.getPreferredEmail();
		Set<String> finaldomain = getDomain(empType, email);

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
			
			if(!userStatus.equalsIgnoreCase("inactive") && !mailStatus.equalsIgnoreCase("inactive") ) {
				log.debug("Email is not for Activation:");
				map.put("emailError", "Please Enter Inactive email for Activation: ");
			}
		    }
		}
		else {
			map.put("emailError", "Please Enter Email For Activation");
		}

		if (previewFormBean.getDor().isEmpty()) {
			map.put("dorError", "Please Enter Date of retirement:");
		}
		if (previewFormBean.getEmpType().isEmpty()) {
			map.put("empTypeError", "Please Enter EmpType:");
		}
		
		if (!previewFormBean.getEmpType().contains("emp_regular") && previewFormBean.getWorkrOrder()==null) {
			map.put("workOrderFileError", "Please upload a workorder file");
			log.debug("need to upload a workorder file");
		}
		
		if(!previewFormBean.getEmpType().contains("emp_regular") && previewFormBean.getWorkrOrder()!=null) {
			
			if (!(previewFormBean.getWorkrOrder() == null) && !previewFormBean.getWorkrOrder().getOriginalFilename().contains(".pdf")) {
				map.put("fileError", "Please upload pdf file only");
			}
			if (previewFormBean.getWorkrOrder().getSize() >= 1048927 ) {
				map.put("fileError", "Please upload less than 1 MB pdf file only");
			}			
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
		EmailActBase emailActBase = fetchDetails(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, emailActBase.getMobile(),
				emailActBase.getName(), "user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType(Constants.STATUS_CA_TYPE);
		status.setStatus(Constants.STATUS_CA_PENDING);
		status.setRecipient(emailActBase.getHodEmail());

		finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
		finalAuditTrack.setToEmail(emailActBase.getHodEmail());

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("{} submitted successfully.", regNumber);
			responseBean
					.setStatus("Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
							+ emailActBase.getHodEmail() + ")");
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
		log.info("EmailAct reject request called by {}",email);
		String formType = "emailactivate";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		EmailActBase emailActBase = fetchDetails(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, emailActBase.getMobile(),
				emailActBase.getName(), "user");

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
		log.info("EmailAct manual upload request");
		String formType = "emailactivate";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		EmailActBase emailActBase = preview(manualUploadBean.getRegNumber());
		String[] contenttype = manualUploadBean.getInfile().getContentType().split("/");
		String ext = contenttype[1];
		String outputfile = new StringBuilder().append(manualUploadBean.getRegNumber()).append("_")
				.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
				.append(".").append(ext).toString();
		
		emailActBase.setPdfPath(fileBasePath+outputfile);
		byte[] bytes = manualUploadBean.getInfile().getBytes();
		Path path = Paths.get(fileBasePath + outputfile);
		boolean stat = emailActbase(emailActBase);
		if(stat) {
			
			Files.write(path, bytes);
		}
		else {
			
			responseBean.setStatus("File failed to upload");
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
   		return responseBean;
		}
		String dn = utilityService.findDn(emailActBase.getEmail());
		String roDn = utilityService.findDn(manualUploadBean.getEmail());
		List<String> aliases = utilityService.aliases(manualUploadBean.getEmail());
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";
		
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), emailActBase.getHodMobile(),
				emailActBase.getHodName(),"usermanual" ,manualUploadBean.getRegNumber());
		status = utilityService.initializeStatusTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), emailActBase.getHodMobile(), emailActBase.getHodName(),"user");
		
         if (emailActBase.getEmployment().equalsIgnoreCase("State") && emailActBase.getState().equalsIgnoreCase("punjab")
				&& emailActBase.getPostingState().equalsIgnoreCase("punjab")) {
			toWhom = "ro";
			daEmail = emailActBase.getHodEmail();
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;
		} else if (utilityService.isNicEmployee(manualUploadBean.getEmail()) && (emailActBase.getPostingState().equalsIgnoreCase("delhi")
				&& emailActBase.getEmployment().equalsIgnoreCase("central")
				&& (dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))) {
			toWhom = "Admin";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} else if (utilityService.isNicEmployee(emailActBase.getEmail()) || utilityService.isNicEmployee(manualUploadBean.getEmail())) {
			toWhom = "ro";
			daEmail = emailActBase.getHodEmail();
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;	
			
		} else if (dn!=null && ((dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))
				&& (roDn.contains("nic support outsourced") || roDn.contains("nationalknowledgenetwork"))) {
			toWhom = "ro";
			daEmail = emailActBase.getHodEmail();
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;
			
		} else if (emailActBase.getEmployment().equalsIgnoreCase("Others")
				&& emailActBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& emailActBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& emailActBase.getCity().equalsIgnoreCase("pune") && (emailActBase.getAddress().toLowerCase().contains("ndc")
						|| emailActBase.getAddress().toLowerCase().contains("national data center"))) {
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
			OrganizationBean org = modelMapper.map(emailActBase, OrganizationBean.class);
				toWhom = "Reporting officer";
				daEmail = emailActBase.getHodEmail();
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
	public boolean emailActbase(EmailActBase emailActBase) {
		EmailActBase details = emailActBaseRepo.save(emailActBase);
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
		System.out.println("email for activation " + preferredEmail1);
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
			 
			 else if(!userStatus.equalsIgnoreCase("inactive") && !mailStatus.equalsIgnoreCase("inactive") ) {
				log.debug("Email is not for Activation:");
				map.put("emailError", "Please Enter Inactive email for Activation");
			}
		    }
			return map;
	}
}
