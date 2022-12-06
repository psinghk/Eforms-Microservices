package in.nic.eForms.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import in.nic.eForms.entities.SingleEmailBase;
import in.nic.eForms.entities.UidCheck;
import in.nic.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.HodDetailsDto;
import in.nic.eForms.models.OrganizationDto;
import in.nic.eForms.models.PreviewFormBean;
import in.nic.eForms.models.ProfileDto;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.repositories.NknSingleEmpCoordRepo;
import in.nic.eForms.repositories.SingleBaseRepo;
import in.nic.eForms.repositories.UidCheckRepo;
import in.nic.eForms.utils.Constants;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SingleEmailUserService {
	private final SingleBaseRepo singleBaseRepo;
	private final NknSingleEmpCoordRepo nknSingleEmpCoordRepo;
	private final UidCheckRepo uidCheckRepo;
	private final Util utilityService;

	@Autowired
	public SingleEmailUserService(SingleBaseRepo singleBaseRepo, UidCheckRepo uidCheckRepo,NknSingleEmpCoordRepo nknSingleEmpCoordRepo,
			Util utilityService) {
		super();
		this.singleBaseRepo = singleBaseRepo;
		this.uidCheckRepo=uidCheckRepo;
		this.utilityService = utilityService;
		this.nknSingleEmpCoordRepo = nknSingleEmpCoordRepo;

	}

	public Set<String> getDomain(@RequestParam String empType, String email) {
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		TreeSet<String> finaldomain = new TreeSet<>();

		try {
			if (profile.getEmployment().equalsIgnoreCase("central")) {
				if (empType.equals("emp_contract")) {
					finaldomain.add("supportgov.in");
				} else if (empType.equals("consultant")) {
					finaldomain.add("govcontractor.in");
					// }//sunny
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
						}
					}
				} else if (profile.getEmployment().equalsIgnoreCase("state")) {
					log.info(":::::2::::" + profile.getEmployment() + ":::::::::::" + profile.getState() + "::::::::::"
							+ profile.getDepartment());
					List<String> temp = utilityService.fetchDomainsByCatAndMinAndDep(profile.getEmployment(),
							profile.getState(), profile.getDepartment());

					for (String string : temp) {
						if (!string.equals("null")) {
							finaldomain.add(string);
							log.info(":::::2::::" + finaldomain);
						}
					}
				} else {
					List<String> temp = utilityService.fetchDomainsByCatAndMin(profile.getEmployment(),
							profile.getMinistry());
					log.info(":::::3::::" + profile.getEmployment() + ":::::::::::" + profile.getOrganization());
					for (String string : temp) {
						if (!string.equals("null")) {
							finaldomain.add(string);
							log.info(":::::3::::" + finaldomain);
						}
					}
				}
				/////////// log.info("::::Domain is Empty so domain is coming from SUN
				/////////// Ldap::::");

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return finaldomain;
	}

	public Set<String> getnknDomain() {
		Set<String> domains = new HashSet<>();
		try {
			domains = nknSingleEmpCoordRepo.fetchdistDomain();
		} catch (Exception e) {
			log.info(e.getMessage());
		} finally {
		}
		return domains;
	}

	public SingleEmailBase fetchDetails(String regNo) {
		return singleBaseRepo.findByRegistrationNo(regNo);
	}

	@Transactional
	public SingleEmailBase insert(SingleEmailBase ldapBase) {
		if (ldapBase != null) {
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String pdate = dateFormat.format(date);
			String oldRegNumber = singleBaseRepo.findLatestRegistrationNo();
			String newRegNumber = "SINGLEUSER-FORM" + pdate;
			if (oldRegNumber == null || oldRegNumber.isEmpty()) {
				newRegNumber += "0001";
			} else {
				String lastst = oldRegNumber.substring(23, oldRegNumber.length());
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
			return singleBaseRepo.save(ldapBase);
		}
		return null;
	}

	public boolean updatePreviewDetails(String regNumber, PreviewFormBean previewFormBean) {
		SingleEmailBase ldapBase = singleBaseRepo.findByRegistrationNo(regNumber);
		if (ldapBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, ldapBase);
				LocalDateTime currentTime = LocalDateTime.now();

				SingleEmailBase singleUpdated = singleBaseRepo.save(ldapBase);
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

	public ResponseBean submitRequest(PreviewFormBean previewFormBean, String ip, String email, String submissionType,
			ResponseBean responseBean) throws ParseException {
		String formType = "single";
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
				if (hmapp.equals("")) {
					log.info("profile:::: " + profile);

					status = utilityService.initializeStatusTable(ip, email, formType, previewFormBean.getRemarks(),
							profile.getMobile(), profile.getName(), "user");

					log.info("status:::: " + status);

					finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType,
							previewFormBean.getRemarks(), profile.getMobile(), profile.getName(), "user", "");

					log.info("finalAuditTrack:::: " + finalAuditTrack);
					ModelMapper modelMapper = new ModelMapper();
					if (previewFormBean.getType().equalsIgnoreCase("eoffice")) {
						log.info("eOffice case:::: ");
						String coordEmail = "rachna_sri@nic.in";
						HodDetailsDto roDetails = utilityService.getHodValues(coordEmail);
						profile.setHodEmail(coordEmail);
						profile.setHodMobile(roDetails.getMobile());
						profile.setHodName(roDetails.getFirstName());
						profile.setHodDesignation(roDetails.getDesignation());
						profile.setHodTelephone(roDetails.getTelephoneNumber());

					}

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

					SingleEmailBase singleEmailBase = modelMapper.map(profile, SingleEmailBase.class);
					log.info("singleEmailBase:1::: " + singleEmailBase);
					singleEmailBase.setPdfPath(submissionType);
					log.info("previewFormBean:1::: " + previewFormBean);
					BeanUtils.copyProperties(previewFormBean, singleEmailBase);
					LocalDateTime currentTime = LocalDateTime.now();
					singleEmailBase.setDatetime(currentTime);
					// ldapBase.setLastUpdationDateTime(currentTime);
					singleEmailBase.setUserIp(ip);

					for (int i = 0; i < 4; i++) {
						singleEmailBase = insert(singleEmailBase);
						log.info("singleEmailBase:2::: " + singleEmailBase);
						if (singleEmailBase.getId() > 0) {
							log.info("singleEmailBase:::: " + singleEmailBase.getId());
							break;
						}
					}

					if (singleEmailBase.getId() > 0) {
						if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
							log.info("online:::: ");
							status.setRegistrationNo(singleEmailBase.getRegistrationNo());
							status.setRecipientType(Constants.STATUS_CA_TYPE);
							status.setStatus(Constants.STATUS_CA_PENDING);
							status.setRecipient(profile.getHodEmail());

							finalAuditTrack.setRegistrationNo(singleEmailBase.getRegistrationNo());
							finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
							finalAuditTrack.setToEmail(profile.getHodEmail());

							if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
								log.info("{} submitted successfully.", singleEmailBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer("
												+ profile.getHodEmail() + ")");
								responseBean.setRegNumber(singleEmailBase.getRegistrationNo());
							} else {
								log.debug("Something went wrong. Please try again after sometime.");
								responseBean.setStatus("Something went wrong. Please try again after sometime.");
								responseBean.setRegNumber("");
							}
						} else {

//				

							status.setRegistrationNo(singleEmailBase.getRegistrationNo());
							status.setRecipientType(Constants.STATUS_USER_TYPE);
							status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
							status.setRecipient(email);

							finalAuditTrack.setRegistrationNo(singleEmailBase.getRegistrationNo());
							finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
							finalAuditTrack.setToEmail(email);

							if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
								log.info("{} submitted successfully.", singleEmailBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
								responseBean.setRegNumber(singleEmailBase.getRegistrationNo());
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

	public String allLdapValues2(String preferredEmail2, String idType, String email) {
		//ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		 String[] words = preferredEmail2.split("\\s+");
		if (utilityService.allLdapValues(preferredEmail2) == null) {
			String []splitString = preferredEmail2.split("@");
			System.out.println("splitted:::"+splitString);
			String uidPreferredEmail2 = splitString[0];
            String domainGenerated = splitString[1];
            
             if (Character.isDigit(preferredEmail2.charAt(0))) {
    			return "Mail cannot start with a numeric value.";
            } else if (preferredEmail2.startsWith("-") || preferredEmail2.startsWith(".")) {
            	return "Mail cannot starts with dot[.] and hyphen[-].";
            } else if (preferredEmail2.contains("_")) {
            	return "Mail cannot contain underscore[_].";
            } else if (preferredEmail2.endsWith(".") || preferredEmail2.endsWith("-") || preferredEmail2.startsWith(".") || preferredEmail2.startsWith("-")) {
            	return "Mail can not start or end with dot[.] and hyphen[-]. ";
            } else if (preferredEmail2.contains("..") || preferredEmail2.contains("--")) {
            	return "Mail can not contain continuous dot[.] or hyphen[-].";
            } else if (!preferredEmail2.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) {
            	return "Mail is not in correct format.";
            } else if (words.length > 1) {
            	return "Mail cannot contain whitespaces.";
            }
            else if (isReservedKeyWord(uidPreferredEmail2)) {
            	return "This mail address " + preferredEmail2 + "  not allowed.";
            }
    		

            else if (idType.equals("id_name")) {
				if (uidPreferredEmail2.contains(".")) {
					System.out.println("boyyahh!! your credential is correct");
				}
				else
				{
					return "Userid must contain dot[.]";
				}
			}
			else if(idType.equals("id_desig"))
			{
				if (uidPreferredEmail2.contains("-")) {
					System.out.println("boyyahh!! your credential is correct");
				}
				else
				{
					return "Userid must contain hyphen[-]";
				}
			}
			return preferredEmail2;
		} 
		
		else {
			return "Login ID (The part before the '@') already exists.";
		}
	}
	public String allLdapValues(String preferredEmail1, String idType, String email) {
		//ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		 String[] words = preferredEmail1.split("\\s+");
		if (utilityService.allLdapValues(preferredEmail1) == null) {
			String []splitString = preferredEmail1.split("@");
			System.out.println("splitted:::"+splitString);
			String uidPreferredEmail1 = splitString[0];
            String domainGenerated = splitString[1];
            
             if (Character.isDigit(preferredEmail1.charAt(0))) {
    			return "Mail cannot start with a numeric value.";
            } else if (preferredEmail1.startsWith("-") || preferredEmail1.startsWith(".")) {
            	return "Mail cannot starts with dot[.] and hyphen[-].";
            } else if (preferredEmail1.contains("_")) {
            	return "Mail cannot contain underscore[_].";
            } else if (preferredEmail1.endsWith(".") || preferredEmail1.endsWith("-") || preferredEmail1.startsWith(".") || preferredEmail1.startsWith("-")) {
            	return "Mail can not start or end with dot[.] and hyphen[-]. ";
            } else if (preferredEmail1.contains("..") || preferredEmail1.contains("--")) {
            	return "Mail can not contain continuous dot[.] or hyphen[-].";
            } else if (!preferredEmail1.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) {
            	return "Mail is not in correct format.";
            } else if (words.length > 1) {
            	return "Mail cannot contain whitespaces.";
            }
            else if (isReservedKeyWord(uidPreferredEmail1)) {
            	return "This mail address " + preferredEmail1 + "  not allowed.";
            }
    		

            else if (idType.equals("id_name")) {
				if (uidPreferredEmail1.contains(".")) {
					System.out.println("boyyahh!! your credential is correct");
				}
				else
				{
					return "Userid must contain dot[.]";
				}
			}
			else if(idType.equals("id_desig"))
			{
				if (uidPreferredEmail1.contains("-")) {
					System.out.println("boyyahh!! your credential is correct");
				}
				else
				{
					return "Userid must contain hyphen[-]";
				}
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
	public Map<String, Object> validateRequest(PreviewFormBean previewFormBean, String email) throws ParseException {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> finalmap = new HashMap<>();
		OrganizationDto organizationDetails = new OrganizationDto();
		// log.info();
//sunny
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		String empType = previewFormBean.getEmpType();
		String preferredEmail1 = previewFormBean.getPreferredEmail1();
		String preferredEmail2 = previewFormBean.getPreferredEmail2();
		String idType= previewFormBean.getIdType();
		Set<String> finaldomain = getDomain(empType, email);
		String ldappreferredEmail1 = allLdapValues1(preferredEmail1);
		String ldappreferredEmail2 = allLdapValues2(preferredEmail2,idType, email);
		List<String> data=utilityService.fetchByEmploymentCategory();
		System.out.println("data:"+data);
		/*
		 * List<String> data1=utilityService.fetchByCentralMinistry(previewFormBean.
		 * getOrganizationCategory()); System.out.println("data2:::"+data1);
		 * List<String> data2=utilityService.fetchByCentralDept(previewFormBean.
		 * getMinistryOrganization()); System.out.println("data2::::"+data2);
		 */
		if (!previewFormBean.getReqUserType().equals("self")) {
			log.info("enter in other");

			/*
			 * if (!utilityService.isNicEmployee(email)) { log.debug("Email", "--");
			 * map.put("Email ", "This facility is provided only to NIC employees"); }
			 */
			
//			if (previewFormBean.getOrganizationCategory() == null
//					|| !utilityService.desigValidation()) {
//				log.debug("ApplicantDesign ", "--");
//				map.put("ApplicantDesignError", "Selected applicant design");
//			}
			

		}

		if (!previewFormBean.getType().equalsIgnoreCase("mail") && !previewFormBean.getType().equalsIgnoreCase("app")
				&& !previewFormBean.getType().equalsIgnoreCase("eoffice")) {
			log.debug("Selected typeOfMailID is not mail or app or eoffice.");
			map.put("typeOfMailIDError", "Selected typeOfMailID is not mail or app or eoffice.");
		}
		if (!previewFormBean.getIdType().equalsIgnoreCase("id_name")
				&& !previewFormBean.getIdType().equalsIgnoreCase("id_desig")) {
			log.debug("Selected emailAddressPreference is neither id_name nor id_desig.");
			map.put("emailAddressPreferenceError", "Selected emailAddressPreference is neither id_name nor id_desig.");
		}
		// emptype=employee description
		if (!previewFormBean.getEmpType().equalsIgnoreCase("emp_regular")
				&& !previewFormBean.getEmpType().equalsIgnoreCase("emp_contract")
				&& !previewFormBean.getEmpType().equalsIgnoreCase("consultant")) {
			log.debug("Selected employeeDescription is not emp_regular or emp_contract or consultant.");
			map.put("employeeDescriptionError",
					"Selected employeeDescription is not emp_regular or emp_contract or consultant.");
		}

		if (ldappreferredEmail1 == null || utilityService.allLdapValues(ldappreferredEmail1) != null) {
			log.debug("Selected preferredEmailAddress1 is not available.");
			map.put("preferredEmailAddress1Error", "Selected preferred Email already exists..Choose a different one.");

		} else {
			String str = (ldappreferredEmail1.substring(ldappreferredEmail1.indexOf("@") + 1));
			// List<String> emailsAgainstMobileList =
			// utilityService.emailsAgainstMobile(profile.getMobile());
			log.info("log :info:domain from profile allowed:::" + finaldomain);
			log.info("domain from profile allowed:::" + finaldomain);
			log.info("log :info:domain from PreferredEmail2 allowed:::" + str);
			log.info("domain from PreferredEmail2 allowed:::" + str);
			if (!finaldomain.contains(str)) {
				log.debug("Domain not allowed.");
				map.put("preferredEmailAddress1DomainError", "Domain not allowed.");

				log.info("does not contain");
			}

			/*
			 * if (emailsAgainstMobileList.size() > 3) { log.debug("ApplicantMobile", "--");
			 * map.put("ApplicantMobileError",
			 * "you can't create a more than 3 id with single phone no."); }
			 */

		}

		if (ldappreferredEmail2 == null || ldappreferredEmail1.equals(ldappreferredEmail2)
				|| utilityService.allLdapValues(ldappreferredEmail2) != null) {
			log.debug("Selected preferredEmailAddress2 is not available.");
			map.put("preferredEmailAddress2Error", "Selected preferred Email already exists..Choose a different one.");

		} else {
			String str = (ldappreferredEmail2.substring(ldappreferredEmail2.indexOf("@") + 1));
			log.info("domain from profile allowed:::" + finaldomain);
			log.info("domain from PreferredEmail2 allowed:::" + str);
			// List<String> emailsAgainstMobileList =
			// utilityService.emailsAgainstMobile(profile.getMobile());
			if (!finaldomain.contains(str)) {
				log.debug("Domain not allowed.");
				map.put("preferredEmailAddress1DomainError", "Domain not allowed.");

				log.info("does not contain");
			}

			/*
			 * if (emailsAgainstMobileList.size() > 3) { log.debug("ApplicantMobile", "--");
			 * map.put("ApplicantMobileError",
			 * "you can't create a more than 3 id with single phone no."); }
			 */

		}

		if (previewFormBean.getDob() == null || !utilityService.dobValidation(previewFormBean.getDob()).equals("")) {
			log.debug("Selected Date of Birth should be more than 18 and less than 67", "--");
			map.put("dateOfBirthError", "Selected Date of Birth should be more than 18 and less than 67");
		}

		if (previewFormBean.getDor() == null || previewFormBean.getDob() == null
				|| !utilityService.dorValidation(previewFormBean.getDor(), previewFormBean.getDob()).equals("")) {
			log.debug("Selected Date of should be greater than current date", "--");
			map.put("dateOfRetirementError", "Selected Date of should be greater than current date");
		}

		if (!previewFormBean.getTnc()) {
			log.debug("Terms and condition is not selected.");
			map.put("tncError", "Please select Terms and Condition to proceed.");
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

	public ResponseBean approve(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Approval of request by user as it was submitted manually");
		String formType = "single";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		SingleEmailBase singleEmailBase = fetchDetails(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, singleEmailBase.getMobile(),
				singleEmailBase.getName(), "user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType(Constants.STATUS_CA_TYPE);
		status.setStatus(Constants.STATUS_CA_PENDING);
		status.setRecipient(singleEmailBase.getHodEmail());

		finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
		finalAuditTrack.setToEmail(singleEmailBase.getHodEmail());

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("{} submitted successfully.", regNumber);
			responseBean
					.setStatus("Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
							+ singleEmailBase.getHodEmail() + ")");
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
		String formType = "single";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		SingleEmailBase singleEmailBase = fetchDetails(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, singleEmailBase.getMobile(),
				singleEmailBase.getName(), "user");

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

}
