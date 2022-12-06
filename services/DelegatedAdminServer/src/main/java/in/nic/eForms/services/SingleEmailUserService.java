package in.nic.eForms.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
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
	public SingleEmailUserService(SingleBaseRepo singleBaseRepo, UidCheckRepo uidCheckRepo,
			NknSingleEmpCoordRepo nknSingleEmpCoordRepo, Util utilityService) {
		super();
		this.singleBaseRepo = singleBaseRepo;
		this.uidCheckRepo = uidCheckRepo;
		this.utilityService = utilityService;
		this.nknSingleEmpCoordRepo = nknSingleEmpCoordRepo;

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
				//}//sunny
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
				///////////System.out.println("::::Domain is Empty so domain is coming from SUN Ldap::::");
				
				
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
