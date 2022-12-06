package in.nic.eForms.services;

import java.security.SecureRandom;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.repositories.SingleUserRepository;
import in.nic.eForms.entities.SingleEmailBase;
import in.nic.eForms.entities.UidCheck;
import in.nic.eForms.entities.User;
import in.nic.eForms.entities.UserAttributes;
import in.nic.eForms.models.AddAliasTrailBean;
import in.nic.eForms.models.ChangeIMAPandPOPTrailBean;
import in.nic.eForms.models.DeactivateTrailBean;
import in.nic.eForms.models.DeleteUserAccountTrailBean;
import in.nic.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.eForms.models.ExchangePrimaryEquivelantTrailBean;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.HodDetailsDto;
import in.nic.eForms.models.MoveToRetiredBOTrailBean;
import in.nic.eForms.models.OrganizationDto;
import in.nic.eForms.models.PersonalDetailsBean;
import in.nic.eForms.models.PreviewFormBean;
import in.nic.eForms.models.ProfileDto;
import in.nic.eForms.models.Quota;
import in.nic.eForms.models.ResetPasswordTrailBean;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.models.SwapSupportContractorTrailBean;
import in.nic.eForms.models.UpdateDateOfExpiryTrailBean;
import in.nic.eForms.models.UserSearchBean;
import in.nic.eForms.models.editPersonalDetailslBean;
import in.nic.eForms.repositories.AddAliasOnUserAccountRepository;
import in.nic.eForms.repositories.ChangeIMAPandPOPRepository;
import in.nic.eForms.repositories.DeactivateUserAccountRespository;
import in.nic.eForms.repositories.DeleteUserAccountRepository;
import in.nic.eForms.repositories.ExchangePrimaryWithEquivalent;
import in.nic.eForms.repositories.MoveToRetiredBORepository;
import in.nic.eForms.repositories.NknSingleEmpCoordRepo;
import in.nic.eForms.repositories.ResetPasswordRepository;
import in.nic.eForms.repositories.SingleBaseRepo;
import in.nic.eForms.repositories.SwapSupportContractorRepository;
import in.nic.eForms.repositories.UidCheckRepo;
import in.nic.eForms.repositories.UpdateAccountExpiryDateRepository;
import in.nic.eForms.repositories.editPersonalDetailsUserRepository;
import in.nic.eForms.utils.BulkValidation;
import in.nic.eForms.utils.Constants;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DelegatedAdminService {
	private final SingleBaseRepo singleBaseRepo;
	private final NknSingleEmpCoordRepo nknSingleEmpCoordRepo;
	private final UidCheckRepo uidCheckRepo;
	private final Util utilityService;
	
	LocalDateTime localDateTime = LocalDateTime.now();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
	long millis = System.currentTimeMillis();
	java.sql.Date date = new java.sql.Date(millis);
	String trailCurrentDate = formatter.format(date);
	
	@Autowired
	Util util;

	@Autowired
	editPersonalDetailsUserRepository editPersonalDetailsUserRepository;
	
	@Autowired
	ResetPasswordRepository ResetPasswordRepository;
	
	@Autowired
	DeleteUserAccountRepository  deleteUserAccountRepository;

	@Autowired
	UpdateAccountExpiryDateRepository  UpdateDateOfExpiryRepository;
	
	@Autowired
	MoveToRetiredBORepository  moveToRetiredBORespository;
	
	@Autowired
	AddAliasOnUserAccountRepository  addAliasOnUserAccountRespository;
	
	@Autowired
	DeactivateUserAccountRespository  deactivateUserAccountRespository;
	
	@Autowired
	ExchangePrimaryWithEquivalent  exchangePrimaryWithEquivalentRespository;
	
	@Autowired
	SwapSupportContractorRepository  swapSupportContractorRespository;
	
	@Autowired
	ChangeIMAPandPOPRepository  changeIMAPandPOPRespository;
	
	 
	@Autowired
	public DelegatedAdminService(SingleBaseRepo singleBaseRepo, UidCheckRepo uidCheckRepo,
			NknSingleEmpCoordRepo nknSingleEmpCoordRepo, Util utilityService) {
		super();
		this.singleBaseRepo = singleBaseRepo;
		this.uidCheckRepo = uidCheckRepo;
		this.utilityService = utilityService;
		this.nknSingleEmpCoordRepo = nknSingleEmpCoordRepo;

	}

	public List<String> DomainFromLdap(String bo) {
		String[] withoutAdminarr = bo.split("-admin");
		String withoutAdmin = withoutAdminarr[0];
		System.out.println("  " + withoutAdmin);

		List<String> domains = new ArrayList<>();
		List<String> domain = utilityService.findDomains(withoutAdmin);
		if (domain.size() > 0) {
			domains.addAll(domain);
		}
		return domains;
	}

	public List<Quota> fetchServicePackageFromLdap(String bo) {
		String[] withoutAdminarr = bo.split("-admin");
		String withoutAdmin = withoutAdminarr[0];
		List<Quota> packageFromLdap = utilityService.fetchServicePackageFromLdap(withoutAdmin);
		System.out.println("packageFromLdap=" + packageFromLdap);
		for (Quota quota : packageFromLdap) {
			quota.getSunAvailableServices();
			quota.getPreferredMailMessageStore();
			quota.getPreferredMailHost();
			quota.getDn();
			quota.getAllowedDomains();
		}
		return packageFromLdap;
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
				}

				else if (profile.getEmployment().equalsIgnoreCase("central")
						|| profile.getEmployment().equalsIgnoreCase("ut")) {
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
				} else {
					List<String> temp = utilityService.fetchDomainsByCatAndMin(profile.getEmployment(),
							profile.getMinistry());
					System.out.println(
							":::::3::::" + profile.getEmployment() + ":::::::::::" + profile.getOrganization());
					for (String string : temp) {
						if (!string.equals("null")) {
							finaldomain.add(string);
							System.out.println(":::::3::::" + finaldomain);
						}
					}
				}
				/////////// System.out.println("::::Domain is Empty so domain is coming from SUN
				/////////// Ldap::::");

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
					/////////// log.info("::::Domain is Empty so domain is coming from SUN
					/////////// Ldap::::");

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		} else {

			/*
			 * log.info(":::4::else::::" +org + ":::::::::::" + min + "::::::::::" + dep);
			 * List<String> temp = utilityService.fetchDomainsByCatAndMinAndDep(org, min,
			 * dep); for (String string : temp) { if (string != null) {
			 * finaldomain.add(string); log.info(":::::1::::" + finaldomain); } }
			 */

			// code from ldap;
			return finaldomain;
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
			long millis = System.currentTimeMillis();
			java.sql.Date date = new java.sql.Date(millis);
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

//	public ResponseBean submitRequest(PreviewFormBean previewFormBean, String ip, String email, String submissionType,
//			ResponseBean responseBean) throws ParseException {
//		String formType = "single";
//		log.info("on submit");
//		Status status = null;
//		FinalAuditTrack finalAuditTrack = null;
//		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
//		responseBean.setRequestType("Submission of request");
//		//Map<String, Object> map = validateRequest(previewFormBean, email);
//		log.info("log:info:Map Size:::: " + map.size());
//		log.info("Map Size:::: " + map.size());
//		log.info("Map:::: " + map);
//		if (map.size() == 0) {
//			responseBean.setErrors(null);
//			log.info("responseBean:::: " + responseBean);
//			if (profile != null) {
//
//				String hmapp = utilityService.checkAvailableEmail(profile.getMobile());
//				System.out.println("hmapp:::available:::" + hmapp);
//				if (hmapp.equals("")) {
//					log.info("profile:::: " + profile);
//
//					
//					log.info("status:::: " + status);
//
//					
//					log.info("finalAuditTrack:::: " + finalAuditTrack);
//					ModelMapper modelMapper = new ModelMapper();
//					if (previewFormBean.getType().equalsIgnoreCase("eoffice")) {
//						log.info("eOffice case:::: ");
//						String coordEmail = "rachna_sri@nic.in";
//						HodDetailsDto roDetails = utilityService.getHodValues(coordEmail);
//						profile.setHodEmail(coordEmail);
//						profile.setHodMobile(roDetails.getMobile());
//						profile.setHodName(roDetails.getFirstName());
//						profile.setHodDesignation(roDetails.getDesignation());
//						profile.setHodTelephone(roDetails.getTelephoneNumber());
//
//					}
//
//
//					SingleEmailBase singleEmailBase = modelMapper.map(profile, SingleEmailBase.class);
//					log.info("singleEmailBase:1::: " + singleEmailBase);
//					singleEmailBase.setPdfPath(submissionType);
//					log.info("previewFormBean:1::: " + previewFormBean);
//					BeanUtils.copyProperties(previewFormBean, singleEmailBase);
//					LocalDateTime currentTime = LocalDateTime.now();
//					singleEmailBase.setDatetime(currentTime);
//					// ldapBase.setLastUpdationDateTime(currentTime);
//					singleEmailBase.setUserIp(ip);
//
//					for (int i = 0; i < 4; i++) {
//						singleEmailBase = insert(singleEmailBase);
//						log.info("singleEmailBase:2::: " + singleEmailBase);
//						if (singleEmailBase.getId() > 0) {
//							log.info("singleEmailBase:::: " + singleEmailBase.getId());
//							break;
//						}
//					}
//
//					if (singleEmailBase.getId() > 0) {
//						if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
//							log.info("online:::: ");
//							status.setRegistrationNo(singleEmailBase.getRegistrationNo());
//							status.setRecipientType(Constants.STATUS_CA_TYPE);
//							status.setStatus(Constants.STATUS_CA_PENDING);
//							status.setRecipient(profile.getHodEmail());
//
//							finalAuditTrack.setRegistrationNo(singleEmailBase.getRegistrationNo());
//							finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
//							finalAuditTrack.setToEmail(profile.getHodEmail());
//
//							if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
//								log.info("{} submitted successfully.", singleEmailBase.getRegistrationNo());
//								responseBean.setStatus(
//										"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer("
//												+ profile.getHodEmail() + ")");
//								responseBean.setRegNumber(singleEmailBase.getRegistrationNo());
//							} else {
//								log.debug("Something went wrong. Please try again after sometime.");
//								responseBean.setStatus("Something went wrong. Please try again after sometime.");
//								responseBean.setRegNumber("");
//							}
//						} else {
//
////				
//
//							status.setRegistrationNo(singleEmailBase.getRegistrationNo());
//							status.setRecipientType(Constants.STATUS_USER_TYPE);
//							status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
//							status.setRecipient(email);
//
//							finalAuditTrack.setRegistrationNo(singleEmailBase.getRegistrationNo());
//							finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
//							finalAuditTrack.setToEmail(email);
//
//							if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
//								log.info("{} submitted successfully.", singleEmailBase.getRegistrationNo());
//								responseBean.setStatus(
//										"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
//								responseBean.setRegNumber(singleEmailBase.getRegistrationNo());
//							} else {
//								log.debug("Something went wrong. Please try again after sometime.");
//								responseBean.setStatus("Something went wrong. Please try again after sometime.");
//								responseBean.setRegNumber("");
//							}
//						}
//					} else {
//						log.debug("Something went wrong. Please try again after sometime.");
//						responseBean.setStatus("Something went wrong. Please try again after sometime.");
//						responseBean.setRegNumber("");
//					}
//
//				} else {
//					if (!(hmapp.equals(""))) {
//						responseBean.setStatus(hmapp);
//					}
//					responseBean.setRegNumber("");
//					responseBean.setStatus("Application could not be submitted.");
//				}
//
//			} else {
//				log.warn(
//						"Hey, {}, We do not have your profile in eForms. Please go to profile section and make your profile first");
//				responseBean.setStatus(
//						"We do not have your profile in eForms. Please go to profile section and make your profile first");
//				responseBean.setRegNumber("");
//			}
//		} else {
//			responseBean.setErrors(map);
//			responseBean.setRegNumber("");
//			responseBean.setStatus("Application could not be submitted.");
//		}
//		return responseBean;
//
//	}
//
	public String allLdapValues2(String preferredEmail2, String idType, String email) {
		// ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		String[] words = preferredEmail2.split("\\s+");
		if (utilityService.allLdapValues(preferredEmail2) == null) {
			String[] splitString = preferredEmail2.split("@");
			System.out.println("splitted:::" + splitString);
			String uidPreferredEmail2 = splitString[0];
			String domainGenerated = splitString[1];

			if (Character.isDigit(preferredEmail2.charAt(0))) {
				return "Mail cannot start with a numeric value.";
			} else if (preferredEmail2.startsWith("-") || preferredEmail2.startsWith(".")) {
				return "Mail cannot starts with dot[.] and hyphen[-].";
			} else if (preferredEmail2.contains("_")) {
				return "Mail cannot contain underscore[_].";
			} else if (preferredEmail2.endsWith(".") || preferredEmail2.endsWith("-") || preferredEmail2.startsWith(".")
					|| preferredEmail2.startsWith("-")) {
				return "Mail can not start or end with dot[.] and hyphen[-]. ";
			} else if (preferredEmail2.contains("..") || preferredEmail2.contains("--")) {
				return "Mail can not contain continuous dot[.] or hyphen[-].";
			} else if (!preferredEmail2.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) {
				return "Mail is not in correct format.";
			} else if (words.length > 1) {
				return "Mail cannot contain whitespaces.";
			} else if (isReservedKeyWord(uidPreferredEmail2)) {
				return "This mail address " + preferredEmail2 + "  not allowed.";
			}

			else if (idType.equals("id_name")) {
				if (uidPreferredEmail2.contains(".")) {
					System.out.println("boyyahh!! your credential is correct");
				} else {
					return "Userid must contain dot[.]";
				}
			} else if (idType.equals("id_desig")) {
				if (uidPreferredEmail2.contains("-")) {
					System.out.println("boyyahh!! your credential is correct");
				} else {
					return "Userid must contain hyphen[-]";
				}
			}
			return preferredEmail2;
		}

		else {
			return "wrong pattern";
		}
	}

	public String allLdapValues(String preferredEmail1, String idType, String email) {
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

			else if (idType.equals("id_name")) {
				if (uidPreferredEmail1.contains(".")) {
					System.out.println("boyyahh!! your credential is correct");
				} else {
					return "Userid must contain dot[.]";
				}
			} else if (idType.equals("id_desig")) {
				if (uidPreferredEmail1.contains("-")) {
					System.out.println("boyyahh!! your credential is correct");
				} else {
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
			responseBean.setStatus("Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
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

	public String getSearchUserInCompleteRepositoryFromLdap(String email) {
		String ldapValues = utilityService.allLdapValues(email);
		return ldapValues;

	}

	                                                                   /*-------------- Sachin Malik ----------------*/
	
	
	/*
	 * Author : Sachin Malik, 
	 * Mehtod Working : Update Date Of Expiry (Update data in LDAP and Save Trail in DA Database.), 
	 * Date : 21-09-2021,
	 */
	
	public String updateDateOfExpiry(String userId, String dateOfExpiry, String dor_remarks, String email, String ip) throws Exception {
		log.debug("Delegated Admin : Update User Account Expiry Date : enter in updateDateOfExpiry Method, with user : "+ userId +" DA Email : "+email +" and DA admin System IP");
		BulkValidation dobValid = new BulkValidation();
		User user = utilityService.getDetailsForEditFromLdap(userId);     /* Get User Data from LDAP Using uid */
		String Dob = user.getNicDateOfBirth();
		String trailDateOfExpiry = user.getNicDateOfRetirement();
		User loginUser = utilityService.getDetailsForEditFromLdap(email.split("@")[0]);    /* Get Login User Data from LDAP Using userl id */
		
		if(dateOfExpiry==null && dateOfExpiry=="") {
			return "Account expiry date cannot be blank";
		}
		else if(Dob!=null && Dob!="") {
			 String result = dobValid.dorValidation(Dob, dateOfExpiry);
			 System.out.println("VALIDATION RESULT STATUS ::::: "+result);
			 if(result.contains("SUCCESS")) {
				 
				/* Update User Date Of Expiry in LDAP Using userAttribute Bean */
					String ldapUpdateStatus = utilityService.updateDateOfExpiry(userId, dobValid.formatDateToSave(dateOfExpiry) ); 
					log.debug("Delegated Admin : Update User Account Expiry Date : LDAP API (/extendDateOfExpiry) Response : "+ ldapUpdateStatus);
					System.out.println("LDAP DATA UPDATE RESULT STATUS ::::: "+ldapUpdateStatus);
					if(ldapUpdateStatus.contains("true")) {
				
				/* Insert Trail of User Account Date Of Expiry in DA Database */
						UpdateDateOfExpiryTrailBean response = updateDateOfExpiryTrail(userId, ip, userId, user.getUserMailStatus(), user.getUserInetStatus(), "active", "active", localDateTime,
								"User Date Of Birth "+Dob, user.getMobile(), trailDateOfExpiry, "sms_mid", email, loginUser.getMobile(), "filename", dor_remarks);
						log.debug("Delegated Admin : Reset Password : Trail insert of Account Expiry Date in DA database response : "+ response.getId());
						return "Account Date Of Expiry update successfully";
						
					}else {
						return "Account Date Of Expiry update unsuccessful.";
					}
			 } else {
				 return result;
			 }
		}else {
			return "Please update date of birth first.";
		}

	}

	/*
	 * Author : Sachin Malik, 
	 * Mehtod Working : Rest Password (Update data in LDAP and Save Trail in DA Database.), 
	 * Date : 20-09-2021,
	 */
	public String resetPassword(String userId, String remarks, String email, String ip ) throws JSONException {   // userId, remark, loginEmail, clientIP
		log.debug("Delegated Admin : Reset Password : enter in resetPassword Method, with user : "+ userId +" DA Email : "+email +" update remarks : "+ remarks +" and DA admin System IP");
		String msg = "";
		User user = utilityService.getDetailsForEditFromLdap(userId);     /* Get User Data from LDAP Using uid */
		System.out.println("USER DETAILS ::::: " + user);
		User loginUser = utilityService.findBymailBean(email);    /* Get Login User Data from LDAP Using email id */
		System.out.println("LOGIN USER DETAILS ::::: " + loginUser);
		String da_mobile =  loginUser.getMobile();
		String user_mobile = user.getMobile();      /* Get User Mobile Number for Send OTP */
		
		if (user == null || utilityService.validateEmail(userId).equalsIgnoreCase("false")) {
			msg = "User does not exist.";
		} else {
			String password = BulkValidation.generateRandomPassword();
			String response = utilityService.resetPassword(userId, password);
			log.debug("Delegated Admin : Reset Password : LDAP API Response : "+ response);
			if (response.equalsIgnoreCase("true")) {
				/*  Insert Trail of User Account Reset Password in DA Database */
				ResetPasswordTrailBean result = saveResetPasswordTrail(loginUser.getUsername(), ip, userId, user_mobile, user_mobile, localDateTime, "sms_mid", email, da_mobile, "filename");
				log.debug("Delegated Admin : Reset Password : Trail insert in DA database response : "+ result.getId());
				msg = "Password has been succesfully reset.";
				return msg;
			} else {
				msg = msg + "Password reset unsuccessful";
				return msg;
			}
		}
		return msg;
	}

	/*
	 * Author : Sachin Malik, 
	 * Mehtod Working : Edit/Update Personal Details of User (Update data in LDAP and Save Trail in DA Database.), 
	 * Date : 20-09-2021  
	 */
	public String editProfile(String uid, PersonalDetailsBean newJsonData, String email, String ip)
			throws JSONException {
		log.debug("Delegated Admin : Edit User Personal Details : enter in editProfile Method, with user : "+ uid +" DA Email : "+email +" and data and DA admin IP");
		String msg = "";
		Map<String, Object> errorObj = new HashMap<>();
		BulkValidation dobValid = new BulkValidation();

		/* Get User Data from LDAP Using uid */
		User user = utilityService.getDetailsForEditFromLdap(uid);
		if (user == null || utilityService.validateEmail(uid).equalsIgnoreCase("false")) {
			msg = "User does not exist.";
			return msg;
		}
		
		/* Set data into variable for DA database trail */
		String ldapFirstName = user.getFirstName();
		String ldapMiddleName = user.getMiddleName();
		String ldapLastName = user.getLastName();
		String ldapCompName = user.getInitials();
		String ldapTelephoneNumber = user.getTelephoneNumber();
		String ldapMobile = user.getMobile();
		String ldapOfficeAddress = user.getOfficeAddress();
		String ldapDesignation = user.getDesignation();
		String ldapNicDateOfBirth = user.getNicDateOfBirth();
		String ldapEmployeeCode = user.getEmployeeCode();
		String dept = newJsonData.getDepartment();
		String displayName=user.getFirstName();
	
		
			UserAttributes userAttributes = new UserAttributes();
			List<String> attributes = new ArrayList<String>();
			
			/* Get Login Person Data from LDAP Using Email Id */
			User DA_Data = utilityService.findBymailBean(email);  //DA DATA
			
			/* firstName */
			if (newJsonData.getFirstName() != null && !newJsonData.getFirstName().isEmpty()) {
				if (ValidationService.isFormatValid("name", newJsonData.getFirstName())) {
					attributes.add("firstName");
					ldapFirstName =  ldapFirstName  + " / " + newJsonData.getFirstName();
					user.setFirstName(newJsonData.getFirstName());
				} else {
					errorObj.put("firstName_err", "First name is not valid.");
				}
			}else {
				errorObj.put("firstName_err", "First name is Mandatory filled.");
			}
			
			/* middleName */
					attributes.add("middleName");
					ldapMiddleName =  ldapMiddleName + " / " + newJsonData.getMiddleName();
					user.setMiddleName(newJsonData.getMiddleName());
					ldapCompName = ldapCompName +" "+ newJsonData.getMiddleName();
			
			/* lastName */
			if (newJsonData.getLastName() != null && !newJsonData.getLastName().isEmpty()) {
				if (ValidationService.isFormatValid("name", newJsonData.getLastName())) {
					attributes.add("lastName");
					ldapLastName = ldapLastName + " / " + newJsonData.getLastName();
					user.setLastName(newJsonData.getLastName());
				} else {
					errorObj.put("lastName_err", "Last name is not valid.");
				}
			} else {
				errorObj.put("lastName_err", "Last name is Mandatory filled.");
			}
			
			/* completeName (cn) */
			attributes.add("cn");
			ldapCompName = user.getInitials() +" "+ user.getFirstName() + " " + user.getMiddleName() +" "+ user.getLastName();
			user.setCn(ldapCompName);

			/* displayName */
			attributes.add("displayName");
			displayName = user.getFirstName();
			user.setDisplayName(displayName);
			
			/* telephoneNumber */
			if (newJsonData.getTelephoneNumber() != null && !newJsonData.getTelephoneNumber().isEmpty()) {
				if (ValidationService.isFormatValid("telephone", newJsonData.getTelephoneNumber())) {
					attributes.add("telephoneNumber");
					ldapTelephoneNumber =  ldapTelephoneNumber + " / " + newJsonData.getTelephoneNumber();
					user.setTelephoneNumber(newJsonData.getTelephoneNumber());
				} else {
					errorObj.put("telephoneNumber_err", "Telephone number is Mandatory filled.");
				}
			} else {
				errorObj.put("telephoneNumber_err", "Telephone number is Mandatory filled.");
			}
			
			/* mobile */
			if (newJsonData.getMobile() != null && !newJsonData.getMobile().isEmpty()) {
				if (ValidationService.isFormatValid("mobile", newJsonData.getMobile())) {
					attributes.add("mobile");
					ldapMobile = ldapMobile  + " / " +  newJsonData.getMobile();
					user.setMobile(newJsonData.getMobile());
				} else {
					errorObj.put("mobile_err", "Mobile number is not valid.");
				}
			} else {
				errorObj.put("mobile_err", "Mobile number is Mandatory filled.");
			}
			
			/* officeAddress */
			if (newJsonData.getOfficeAddress() != null && !newJsonData.getOfficeAddress().isEmpty()) {
				if (ValidationService.isFormatValid("address", newJsonData.getOfficeAddress())) {
					attributes.add("officeAddress");
					ldapOfficeAddress =  ldapOfficeAddress + " / " + newJsonData.getOfficeAddress();
					user.setOfficeAddress(newJsonData.getOfficeAddress());
				} else {
					errorObj.put("officeAddress_err", "Office Address is not valid.");
				}
			} else {
				errorObj.put("officeAddress_err", "Office Address is Mandatory filled.");
			}
			
			/* designation */
			if (newJsonData.getDesignation() != null && !newJsonData.getDesignation().isEmpty()) {
				attributes.add("designation");
				ldapDesignation =  ldapDesignation + " / " + newJsonData.getDesignation();
				user.setDesignation(newJsonData.getDesignation());
			} else {
				errorObj.put("designation_err", "Designation is Mandatory filled.");
			}
			
			/* dateOfBirth */
			if (newJsonData.getNicDateOfBirth() != null && newJsonData.getNicDateOfBirth() != "") {
				HashMap<String, String> dobValidMap = dobValid.DOB(newJsonData.getNicDateOfBirth());

				System.out.println("NIC DATE OF BIRTH ::::: " + newJsonData.getNicDateOfBirth());
				if (dobValidMap.get("valid").contains("true")) {
					ldapNicDateOfBirth =  ldapNicDateOfBirth + " / " + dobValid.formatDateToShow(dobValidMap.get("fdob"));
					user.setNicDateOfBirth(dobValid.formatDateToSave(newJsonData.getNicDateOfBirth()));
					attributes.add("dateOfBirth");
				} else {
					errorObj.put("nicDateOfBirth_err", dobValidMap.get("errorMsg"));
				}
			} else {
				errorObj.put("nicDateOfBirth_err", "Date of birth is Mandatory filled.");
			}
			
			/* employeecode */
			if (newJsonData.getEmployeeCode() != null && !newJsonData.getEmployeeCode().isEmpty()) {
				if (ValidationService.isFormatValid("employeecode", newJsonData.getEmployeeCode())) {
					attributes.add("employeecode");
					ldapEmployeeCode =  ldapEmployeeCode + " / " +  newJsonData.getEmployeeCode();
					user.setEmployeeCode(newJsonData.getEmployeeCode());
				} else {
					errorObj.put("employeecode_err", "Employee Code is not valid.");
				}
			} else {
				errorObj.put("employeecode_err", "Employee Code is Mandatory filled.");
			}
			if (attributes.isEmpty()) {
				msg = "No attributes updated.";
				return msg;
			}
			userAttributes.setEmail(user.getEmail());
			userAttributes.setRemarks("Updated By : " + email + " IP : " + ip);
			userAttributes.setUsername(uid);
			userAttributes.setUser(user);
			userAttributes.setAttributes(attributes);

			if(errorObj.size()<=0) {
				String response = utilityService.updateThroughMail(userAttributes);
				log.debug("Delegated Admin : Edit User Personal Details : LDAP API Response : "+ response);
				if (response.equalsIgnoreCase("true")) {
					System.out.println("RESPONSE ::::: " + response);
				    System.out.println("LOCALDATETIME ::::: "+localDateTime);
				    /*  Insert Trail of User Account Edit Personal Details in DA Database */
				    editPersonalDetailslBean result = saveTrail(user.getEmail(), uid, ldapFirstName, ldapMiddleName, ldapLastName, ldapMobile, ldapTelephoneNumber, ldapDesignation, dept, ldapOfficeAddress,
				    		ldapEmployeeCode, ip, localDateTime, "SMS_MID", user.getMobile(), email, DA_Data.getMobile());
				    log.debug("Delegated Admin : Edit User Personal Details : TRIAL Data update result id ::::: "+result.getId());
				    System.out.println("TRIAL DATA UPDATE RESULT ID ::::: "+result.getId());
				    msg = "Successfully updated these attributes : " + attributes + ". \nTRAIL Update Id : " + result.getId()+ ". \nLDAP Update Status : " + response;
			   } 
			} else {
				return "Error : " + errorObj.toString();
			}
			
			return msg;
	}
	
	/*
	 * Author : Sachin Malik, 
	 * Mehtod Working : Delete Account of User (Update data in LDAP and Save Trail in DA Database.), 
	 * Date : 22-09-2021  
	 */
	public String deleteUserAccount(String userId, String remarks, String email, String ip ) throws JSONException {   // userId, remark, loginEmail, clientIP
		log.debug("Delegated Admin : Delete User Account : enter in deleteUserAccount Method, with user : "+ userId +" remarks : "+remarks+" DA Email : "+email);
		String msg = "";
		UserSearchBean user = utilityService.getDetailsFromLdap(userId);     /* Get User Data from LDAP Using uid */
		System.out.println("USER DETAILS ::::: " + user);   
		User loginUser = utilityService.getDetailsForEditFromLdap(email.split("@")[0]);    /* Get Login User Data from LDAP Using email id */
		System.out.println("LOGIN USER DETAILS ::::: " + loginUser);
		if (user == null || utilityService.validateEmail(userId).equalsIgnoreCase("false")) {
			msg = "User does not exist.";
		}else {
			user.setRemarks(remarks);
			Boolean response = utilityService.deleteUserAccount(user);  /* Delete User Data from LDAP */
			log.debug("Delegated Admin : Delete User Account : LDAP API Response : "+ response);
			if (response) {
				/*  Insert Trail of User Account Delete in DA Database */
				DeleteUserAccountTrailBean result = deleteUserAccountTrail(loginUser.getUsername(), ip, user.getUsername(), user.getEmail(), user.getMobile(), localDateTime,
						                                                    loginUser.getEmail(), loginUser.getMobile());
				log.debug("Delegated Admin : Delete User Account : Trail insert in DA Database Response : "+ result);
				if(result!=null) {
					msg = "Account has been delete succesfully.";					
				}else {
					msg = "Account delete unsuccessful.";
				}
			} else {
				msg = msg + "Account delete unsuccessful.";
				return msg;
			}
		}
		return msg;
	}
	
	
	/*
	 * Author : Sachin Malik, 
	 * Mehtod Working : Move to Retired BO Account of User (Update data in LDAP and Save Trail in DA Database.), 
	 * Date : 24-09-2021  
	 */
	public String moveToRetiredBoUserAccount(String userId,String remarks, String email, String ip ) throws JSONException {   // userId, remark, loginEmail, clientIP
		log.debug("Delegated Admin : Move to Retired BO Account : enter in deleteUserAccount Method, with user : "+ userId +" DA Email : "+email);
		String msg = "";
		User user = utilityService.getDetailsForEditFromLdap(userId);     /* Get User Data from LDAP Using uid */
		System.out.println("USER DETAILS ::::: " + user);   
		User loginUser = utilityService.getDetailsForEditFromLdap(email.split("@")[0]);    /* Get Login User Data from LDAP Using uid */
		System.out.println("LOGIN USER DETAILS ::::: " + loginUser);	
		if (user == null || utilityService.validateEmail(userId).equalsIgnoreCase("false")) {
			msg = "User does not exist.";
		} else {
			String response = null;
			try {
				response = utilityService.moveToRetiredBOAccount(userId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.debug("Delegated Admin : Move to Retired BO Account : LDAP API Response : "+ response);
			System.out.println("LDAP RESPONSE ::::: "+response);
			if (response!=null) {
				/*  Get Dn from LDAP for save in Trail */
				 String new_dn = utilityService.findDn(userId, user.getEmail());
				/*  Insert Trail of Move to Retired BO Account in DA Database */
				MoveToRetiredBOTrailBean result = moveToRetiredBoUserAccountTrail(email, loginUser.getMobile(), user.getDn(), new_dn, userId, "old_po", "old_bo", remarks, localDateTime);
				log.debug("Delegated Admin : Move to Retired BO Account : Trail Update Response : "+ result);
				if(result!=null) {
					msg = "Retired BO account has been moved succesfully in LDAP.";					
					 }else {
					     msg = "Retired BO account moved unsuccessful. please try again...";
					 }
			} else {
				msg = msg + "Retired BO account moved unsuccessful. please try again...";
				return msg;
			}
			
		}
		return msg;
	}
	
	
	/*
	 * Author : Sachin Malik, 
	 * Mehtod Working : Add Mail Equivalents of BO User Account (Update data in LDAP and Save Trail in DA Database.), 
	 * Date : 24-09-2021  
	 */
	public String addAliasOnUserAccount(String userId, String alias, String email, String ip ) throws JSONException {   // userId, remark, loginEmail, clientIP
		log.debug("Delegated Admin : Add Mail Equivalents of BO User Account : enter in addAliasOnUserAccount Method, with user : "+ userId +" Add/update alias : "+alias+" By DA Email : "+email);
		String msg = "";
		User user = utilityService.getDetailsForEditFromLdap(userId);     /* Get User Data from LDAP Using uid */
		System.out.println("USER DETAILS ::::: " + user);   
		User loginUser = utilityService.getDetailsForEditFromLdap(email.split("@")[0]);    /* Get Login User Data from LDAP Using User id */
		System.out.println("LOGIN USER DETAILS ::::: " + loginUser);
		if (user == null || utilityService.validateEmail(userId).equalsIgnoreCase("false")) {
			msg = "User does not exist.";
		} else if(!ValidationService.isFormatValid("email",alias)){
			msg= "Please enter valid alias.";
		} else {  
			String response = utilityService.AddAlias(userId,alias);   /* Add Alias on User Account in LDAP */
			log.debug("Delegated Admin : Add Alias on User Account : LDAP API Response : "+ response);
			
			if (response!=null) {
				/*  Insert Trail of Add Alias On User Account in DA Database */
				AddAliasTrailBean result = addAliasTrail(userId, ip, user.getEmail(), alias, localDateTime, "sms_mid", user.getMobile(), email, loginUser.getMobile());
				log.debug("Delegated Admin : Add Alias on User Account : Trail Update Response : "+ result);
				if(result!=null) {
					msg = "Alias in User Account has been add succesfully.";	
					  }else { 
						  msg = "Alias in User Account unsuccessful. please try again ..."; 
						  }
			} else {
				msg = "Add Alias Account unsuccessful. please try again ...";
				return msg;
			}
		}
		return msg;
	}
	
	/*
	 * Author : Sachin Malik, 
	 * Mehtod Working : De-Activate User Account (Update data in LDAP and Save Trail in DA Database.), 
	 * Date : 28-09-2021,
	 */
	public String deactivateUserAccount(@Valid String uid, @Valid String remarks, @Valid String loginEmail, @Valid String clientIP) throws JSONException {
		log.debug("Delegated Admin : Deactivate user account : enter in deactivateUserAccount Method, with user : "+ uid +" DA Email : "+loginEmail);
		String msg = "";
		User user = utilityService.getDetailsForEditFromLdap(uid);     /* Get User Data from LDAP Using uid */
		System.out.println("USER DETAILS ::::: " + user);
		User loginUser = utilityService.getDetailsForEditFromLdap(loginEmail.split("@")[0]);    /* Get Login User Data from LDAP Using User id */
		System.out.println("LOGIN USER DETAILS ::::: " + loginUser);
		if (user == null || utilityService.validateEmail(uid).equalsIgnoreCase("false")) {
			msg = "User does not exist.";
		} else if (user.getUserInetStatus().contains("inactive") && user.getUserMailStatus().contains("inactive") ) {
			msg = "User account already deactivated.";			
		}
		else {  
			String response = utilityService.deactivateAccount(uid);   /* Deactivate User Account in LDAP */
			log.debug("Delegated Admin : Deactivate User Account : LDAP API Response : "+ response);
			System.out.println("Delegated Admin : Deactivate User Account : LDAP API Response : "+response);
			if (response.contains("true")) {
				/* Insert Trail of Deactivate User Account in DA Database  */
				DeactivateTrailBean result = deactivateTrailBean(loginEmail.split("@")[0], clientIP, uid, user.getEmail(), user.getMobile(), localDateTime, loginEmail, user.getMobile(), 
						user.getFirstName(), "Deactivate Reason : "+remarks, "sms_mid" );
				log.debug("Delegated Admin : Deactivate User Account : Trail Update Response : "+ result);
				if(result!=null) {
					msg = "User Account has been deactivate succesfully.";	
					  }else { 
						  msg = "Account Deactivate unsuccessful. Please try again..."; 
						  }
			} else {
				msg = "Account Deactivate unsuccessful. Please try again.";
				return msg;
			}
		}
		
		return msg;
	}
	
	/*
	 * Author : Sachin Malik, 
	 * Mehtod Working : Exchange with Primary Equivalent (Update data in LDAP and Save Trail in DA Database.), 
	 * Date : 28-09-2021,
	 */
	public String exchangePrimaryWithEquivalent(@Valid String uid, @Valid String newPrimaryEquivalent, @Valid String remarks, @Valid String loginEmail, @Valid String clientIP) throws JSONException {
		log.debug("Delegated Admin : Exchange with Primary Equivalent : enter in exchangePrimaryWithEquivalent Method, with user : "+ uid +" DA Email : "+loginEmail);
		String msg = "";
		User user = utilityService.getDetailsForEditFromLdap(uid);     /* Get User Data from LDAP Using uid */
		System.out.println("USER DETAILS ::::: " + user);
		String old_primary = user.getEmail();
		List<String> old_alias = user.getAliases();  /* Old Alias For Trail */
		User loginUser = utilityService.getDetailsForEditFromLdap(loginEmail.split("@")[0]);    /* Get Login User Data from LDAP Using User id */
		System.out.println("LOGIN USER DETAILS ::::: " + loginUser);
		
		if (user == null || utilityService.validateEmail(uid).equalsIgnoreCase("false")) {
			msg = "User does not exist.";
			return msg;
		} 
		/* New Primary Equivalent is already same as Old Equivalent. if condition check if true then create only trail in DA Database not LDAP API Call */
		if (old_primary.contains(newPrimaryEquivalent)) {			
			ExchangePrimaryEquivelantTrailBean result = exchangePrimaryEquivelantTrailBean( loginUser.getUsername(), clientIP, uid, old_primary, newPrimaryEquivalent, old_alias, old_alias, localDateTime, "failed", user.getMobile(), loginUser.getEmail(), loginUser.getMobile());
			log.debug("Delegated Admin : Exchange Primary Equivalent : Trail Update Response : "+ result);
			if(result!=null) {
			      msg = "Exchange with Primary Equivalent unsuccessful Because new primary equivalent is already user account primary equivalent.";
			} else {
				msg = "Please try again...";
			}
		} else if(!(old_alias.toString()).contains(newPrimaryEquivalent)) {
			ExchangePrimaryEquivelantTrailBean result = exchangePrimaryEquivelantTrailBean( loginUser.getUsername(), clientIP, uid, old_primary, newPrimaryEquivalent, old_alias, old_alias, localDateTime, "failed", user.getMobile(), loginUser.getEmail(), loginUser.getMobile());
			log.debug("Delegated Admin : Exchange Primary Equivalent : Trail Update Response : "+ result);
			if(result!=null) {
			      msg = "Exchange with Primary Equivalent unsuccessful Because new primary equivalent is not avilable in your alias. Please add alias first.";
			} else {
				msg = "Please try again...";
			}
		}
		else {
			String response = utilityService.exchangePrimaryWithAlias(uid, newPrimaryEquivalent);   /* Exchange with Primary Equivalent in LDAP */
			log.debug("Delegated Admin : Exchange with Primary Equivalent : LDAP API Response : "+ response);
			System.out.println("Delegated Admin : Exchange with Primary Equivalent : LDAP API Response : "+response);
			if (response.contains("true")) {
				/*  Insert Trail of Exchange with Primary Equivalent in DA Database  */
				User userForTrail = utilityService.getDetailsForEditFromLdap(uid);
				List<String> new_alias = userForTrail.getAliases();   /* New Alias For Trail */
				ExchangePrimaryEquivelantTrailBean result = exchangePrimaryEquivelantTrailBean( loginUser.getUsername(), clientIP, uid, old_primary, newPrimaryEquivalent, old_alias, new_alias, localDateTime, "sms_mid", user.getMobile(), loginUser.getEmail(), loginUser.getMobile());
				log.debug("Delegated Admin : Exchange Primary Equivalent : Trail Update Response : "+ result);
				if(result!=null) {
					msg = "Primary Equivalent has been changed succesfully.";	
					  }else { 
						  msg = "Exchange Primary Equivalent unsuccessful. please try again ..."; 
						  }
			}else {
				msg = "Exchange with Primary Equivalent unsuccessful. Please try again.";
				return msg;
			}
		}
		
		return msg;
	}
	
	/*
	 * Author : Sachin Malik, 
	 * Mehtod Working : Swap to supportgov/govcontractor User Account (Update data in LDAP and Save Trail in DA Database.), 
	 * Date : 29-09-2021,
	 */
	public String swapDomainInPrimary(@Valid String uid, @Valid String newDominForSwap, @Valid String loginEmail, @Valid String clientIP) throws JSONException {
		log.debug("Delegated Admin : Swap to supportgov/govcontractor user account : enter in deactivateUserAccount Method, with user : "+ uid +" DA Email : "+loginEmail);
		String msg = "";
		User user = utilityService.getDetailsForEditFromLdap(uid);     /* Get User Data from LDAP Using uid */
		System.out.println("USER DETAILS ::::: " + user);
		User loginUser = utilityService.getDetailsForEditFromLdap(loginEmail.split("@")[0]);    /* Get Login User Data from LDAP Using User id */
		System.out.println("LOGIN USER DETAILS ::::: " + loginUser);
		String prev_primary = user.getEmail();
		String newPrimaryEmail = uid+newDominForSwap;
		
		if (user == null || utilityService.validateEmail(uid).equalsIgnoreCase("false")) {
			msg = "User does not exist.";
		} else if (user.getUserInetStatus().contains("inactive") && user.getUserMailStatus().contains("inactive") ) {
			msg = "Deactivated Account";			
		} else if (newPrimaryEmail.contains(prev_primary)) {
			msg = "Swap to Supportgov/govcontractor unsuccessful Because new domin is already user account primary domin.";
		}
		else {  
			String response = utilityService.exchangePrimaryWithAlias(uid, newPrimaryEmail);   /* Swap to supportgov/govcontractor User Account in LDAP */
			log.debug("Delegated Admin : Swap to supportgov/govcontractor User Account : LDAP API Response : "+ response);
			System.out.println("Delegated Admin : Swap to supportgov/govcontractor User Account : LDAP API Response : "+response);
			if (response.contains("true")) {
				/* Insert Trail of Swap to supportgov/govcontractor User Account in DA Database  */
				SwapSupportContractorTrailBean result = swapSupportContractorTrailBean(loginUser.getUsername(), loginUser.getEmail(), user.getEmail(), uid, "Domin_Changed", localDateTime, prev_primary, newPrimaryEmail, "sms_mid", loginUser.getCn()); 
				log.debug("Delegated Admin : Swap to supportgov/govcontractor User Account : Trail Update Response : "+ result); 
				if(result!=null) {
					msg = "User Account has been change succesfully."; 
					} else { 
						msg = "User account swap unsuccessful. Please try again..."; 
					}
			} else {
				msg = "User account swap unsuccessful. Please try again...";
				return msg;
			}
		}
		
		return msg;
	}
	
	/*
	 * Author : Sachin Malik, 
	 * Mehtod Working : Change IMAP/POP Status of User Account (Update data in LDAP and Save Trail in DA Database.), 
	 * Date : 29-09-2021,
	 */
	public String changeIMAPandPOP(@Valid String uid, @Valid String imapPOP,@Valid String remark, @Valid String loginEmail, @Valid String clientIP) throws JSONException {
		log.debug("Delegated Admin : Change IMAP/POP user account : enter in deactivateUserAccount Method, with user : "+ uid +" DA Email : "+loginEmail);
		String msg = "";
		User user = utilityService.getDetailsForEditFromLdap(uid);     /* Get User Data from LDAP Using uid */
		System.out.println("USER DETAILS ::::: " + user);
		User loginUser = utilityService.getDetailsForEditFromLdap(loginEmail.split("@")[0]);    /* Get Login User Data from LDAP Using User id */
		System.out.println("LOGIN USER DETAILS ::::: " + loginUser);
		String old_value = user.getMailallowedserviceaccess();
		String IMAP = "";
		String POP = "";
		String responseIMAP = "";
		String responsePOP = "";
		if (user == null || utilityService.validateEmail(uid).equalsIgnoreCase("false")) {
			msg = "User does not exist.";
		} else if (user.getUserInetStatus().contains("inactive") && user.getUserMailStatus().contains("inactive") ) {
			msg = "Deactivated Account";			
		} else {  
			if(imapPOP.contains("imap")) {
				responseIMAP = utilityService.enableIMAP(uid);
				IMAP = "IMAP Enable";
				responsePOP = utilityService.disablePOP(uid);
				POP = "POP Disable";

			}
			else if(imapPOP.contains("pop")) {
				responseIMAP = utilityService.disablePOP(uid);
				IMAP = "IMAP Disable";
				responsePOP = utilityService.enablePOP(uid);
				POP = "POP Enable";
			}
			String action = IMAP+" & "+POP;
			System.out.println("Delegated Admin : Change IMAP/POP User Account : LDAP IMAP API Response : "+responseIMAP+" LDAP POP API Response : "+responsePOP);
			if (responseIMAP.contains("true") && responsePOP.contains("true")) {
				/* Insert Trail of Change IMAP/POP User Account in DA Database  */
				User trailUser = utilityService.getDetailsForEditFromLdap(uid);
				ChangeIMAPandPOPTrailBean result = changeIMAPandPOPTrailBean(loginUser.getUsername(), clientIP, uid, old_value, trailUser.getMailallowedserviceaccess(), action, 
						"sms_mid",user.getMobile(), loginUser.getEmail(), loginUser.getMobile(), "filename", remark, "", "", "", localDateTime, clientIP, "", "", "", "", user.getEmail(), user.getMobile() ); 
				
				log.debug("Delegated Admin : Swap to Change IMAP/POP User Account : Trail Update Response : "+ result); 
				if(result!=null) {
					msg = "User Account IMAP/POP Status has been change succesfully."; 
					} else { 
						msg = "User account IMAP/POP Status swap unsuccessful. Please try again..."; 
					}
			} else {
				msg = "User account IMAP/POP Status swap unsuccessful.";
				return msg;
			}
		}
		
		return msg;
	}
	
	//Trail's for DA Database...

	/*  Method : Insert Trail of Edit Personal Details in DA Database  */
	public editPersonalDetailslBean saveTrail(String loginId, String uid, String oldnew_fname, String oldnew_mname, String oldnew_lname, String oldnew_Mobile, String oldnew_telephone, String oldnew_designation,
			String oldnew_department, String oldnew_address, String oldnew_empnum, String remoteIp, LocalDateTime localDateTime2, String smsMid, String sms_mobile, String da_mail, String da_mobile) {
		editPersonalDetailslBean result = editPersonalDetailsUserRepository.save(new editPersonalDetailslBean(loginId, uid, oldnew_fname, oldnew_mname, oldnew_lname,
						oldnew_Mobile, oldnew_telephone, oldnew_designation, oldnew_department, oldnew_address, oldnew_empnum, remoteIp, localDateTime2, smsMid, sms_mobile, da_mail, da_mobile));
		return result;

	}
	
	/*  Method : Insert Trail of Reset Password in DA Database  */
	  public ResetPasswordTrailBean saveResetPasswordTrail(String login_id, String remote_ip, String user_id, String old_mobile, String new_mobile, LocalDateTime datetime, String sms_mid, String da_mail, String da_mobile, String filename) { 
		 ResetPasswordTrailBean result =  ResetPasswordRepository.save(new ResetPasswordTrailBean(login_id, remote_ip, user_id, old_mobile, new_mobile, datetime, sms_mid, da_mail, da_mobile, filename)); 
		 return result; 
	  
	  }
	 
	  /*  Method : Insert Trail of Delete User Account in DA Database  */
	  public DeleteUserAccountTrailBean deleteUserAccountTrail(String uid, String remote_ip, String user_id, String user_mail, String user_mobile, LocalDateTime datetime, String da_mail, String da_mobile) { 
		  DeleteUserAccountTrailBean result =  deleteUserAccountRepository.save(new DeleteUserAccountTrailBean(uid, remote_ip, user_id, user_mail, user_mobile, datetime, da_mail, da_mobile)); 
		 return result; 
	  
	  }
	  
	  /*  Method : Insert Trail of Update Date Of Expiry in DA Database  */
	  public UpdateDateOfExpiryTrailBean updateDateOfExpiryTrail(String uid, String remote_ip, String user_id, String old_mailstatus, String old_inetstatus, String updated_mailstatus, String updated_inetstatus, LocalDateTime datetime,
				String remarks, String sms_mobile, String old_exp_dor, String sms_mid, String da_mail, String da_mobile, String filename, String dor_remarks) { 
		  UpdateDateOfExpiryTrailBean result =  UpdateDateOfExpiryRepository.save(new UpdateDateOfExpiryTrailBean(uid, remote_ip, user_id, old_mailstatus, old_inetstatus, updated_mailstatus, updated_inetstatus, datetime,
					remarks, sms_mobile, old_exp_dor, sms_mid, da_mail, da_mobile, filename, dor_remarks)); 
		 return result; 
	  
	  }
	  
	  /*  Method : Insert Trail of Retired to  User Account in DA Database  */
	  public MoveToRetiredBOTrailBean moveToRetiredBoUserAccountTrail(String da_mail, String da_mobile, String user_dn, String new_dn, String uid, String old_po, String old_bo, String remarks, LocalDateTime datetime) { 
		  MoveToRetiredBOTrailBean result =  moveToRetiredBORespository.save(new MoveToRetiredBOTrailBean(da_mail, da_mobile, user_dn, new_dn, uid, old_po, old_bo, remarks, datetime)); 
		 return result; 
	  
	  }
	  
	  /*  Method : Insert Trail of Retired to  User Account in DA Database  */
	  public AddAliasTrailBean addAliasTrail(String login_id, String remote_ip, String user_email, String add_equivalentaddress, LocalDateTime datetime, String sms_mid, String sms_mobile, String da_mail, String da_mobile) { 
		  AddAliasTrailBean result =  addAliasOnUserAccountRespository.save(new AddAliasTrailBean(login_id, remote_ip, user_email, add_equivalentaddress, datetime, sms_mid, sms_mobile, da_mail, da_mobile)); 
		 return result; 
	  
	  }
	  
	  /*  Method : Insert Trail of Retired to  User Account in DA Database  */
	  public DeactivateTrailBean deactivateTrailBean(String login_id, String remote_ip, String uid, String user_mail, String user_mobile,
				LocalDateTime datetime, String da_mail, String da_mobile, String bo_name, String remarks, String sms_mid) { 
		  DeactivateTrailBean result =  deactivateUserAccountRespository.save(new DeactivateTrailBean(login_id, remote_ip, uid, user_mail, user_mobile, datetime, da_mail, da_mobile, bo_name, remarks, sms_mid)); 
		 return result; 
	  
	  }
	  
	  /*  Method : Insert Trail of Exchange with Primary Equivalent in DA Database  */
	  public ExchangePrimaryEquivelantTrailBean exchangePrimaryEquivelantTrailBean(String login_id, String remote_ip, String uid, String old_primary, String new_primary, List<String> old_alias, List<String> new_alias, 
				LocalDateTime datetime, String sms_mid, String sms_mobile, String da_email, String da_mobile) { 
		  ExchangePrimaryEquivelantTrailBean result =  exchangePrimaryWithEquivalentRespository.save(new ExchangePrimaryEquivelantTrailBean(login_id, remote_ip, uid, old_primary, new_primary, old_alias.toString(), new_alias.toString(), 
					datetime, sms_mid, sms_mobile, da_email, da_mobile)); 
		 return result; 
	  
	  }
	  
	  /*  Method : Insert Trail of Swap to supportgov/govcontractor in DA Database  */
	  public SwapSupportContractorTrailBean swapSupportContractorTrailBean(String da_login_id, String da_mail, String user_mail, String uid, String action, LocalDateTime datetime, String prev_primary, String curr_primary, String sms_mid, String bo_name) { 
		  SwapSupportContractorTrailBean result =  swapSupportContractorRespository.save(new SwapSupportContractorTrailBean(da_login_id, da_mail, user_mail, uid, action, datetime, prev_primary, curr_primary, sms_mid, bo_name)); 
		 return result; 
	  
	  }
	  
	  /*  Method : Insert Trail of Change IMAP and POP of User Account in DA Database  */
	  public ChangeIMAPandPOPTrailBean changeIMAPandPOPTrailBean(String login_id, String remote_ip, String user_id, String old_value, String new_value, String action, String sms_mid, String sms_mobile, String da_email, String da_mobile,
				String filename, String remark, String admin_email, String admin_mobile, String admin_name, LocalDateTime datetime, String ip, String mid, String new_protocol, String old_protocol, String remarks, String user_mail, String user_mobile) { 
		  ChangeIMAPandPOPTrailBean result =  changeIMAPandPOPRespository.save(new ChangeIMAPandPOPTrailBean(login_id, remote_ip, user_id, old_value,new_value, action, sms_mid, sms_mobile, da_email, da_mobile,
					filename, remark, datetime, ip, user_mail, user_mobile)); 
		 return result; 
	  
	  }
	

}
