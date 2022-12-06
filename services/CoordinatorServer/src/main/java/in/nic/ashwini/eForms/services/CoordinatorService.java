package in.nic.ashwini.eForms.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.entities.EmailCoordinators;
import in.nic.ashwini.eForms.entities.OrganizationBean;
import in.nic.ashwini.eForms.entities.PunjabCoordinators;
import in.nic.ashwini.eForms.entities.VpnCoordinators;
import in.nic.ashwini.eForms.repositories.EmailCoordinatorsRepository;
import in.nic.ashwini.eForms.repositories.PunjabCoordinatorsRepository;
import in.nic.ashwini.eForms.repositories.VpnCoordinatorsRepository;

@Service
public class CoordinatorService {

	private final EmailCoordinatorsRepository emailCoordinatorsRepository;
	private final VpnCoordinatorsRepository vpnCoordinatorsRepository;
	private final PunjabCoordinatorsRepository punjabCoordinatorsRepository;
	private final UtilityService utilityService;

	@Autowired
	public CoordinatorService(EmailCoordinatorsRepository emailCoordinatorsRepository,
			VpnCoordinatorsRepository vpnCoordinatorsRepository, UtilityService utilityService,
			PunjabCoordinatorsRepository punjabCoordinatorsRepository) {
		super();
		this.emailCoordinatorsRepository = emailCoordinatorsRepository;
		this.vpnCoordinatorsRepository = vpnCoordinatorsRepository;
		this.utilityService = utilityService;
		this.punjabCoordinatorsRepository = punjabCoordinatorsRepository;
	}

	public boolean isUserCo(String ip, String email) {
		Set<String> aliases = utilityService.fetchAliasesFromLdap(email);
		for (String mail : aliases) {
			List<EmailCoordinators> co = emailCoordinatorsRepository.findByEmailAndStatusAndVpnIpContaining(mail, "a",
					ip);
			if (co.size() > 0) {
				return true;
			}
		}
		return false;
	}

	public boolean isUserVpnCo(String email) {
		Set<String> aliases = utilityService.fetchAliasesFromLdap(email);
		for (String mail : aliases) {
			String sMail = (String) mail;
			Optional<VpnCoordinators> vpnCo = vpnCoordinatorsRepository.findFirstByEmailAndStatus(sMail, "a");
			if (vpnCo.isPresent()) {
				return true;
			}
		}
		return false;
	}

	public List<String> fetchHimachalCoords(@NotEmpty String department) {
		List<String> himachalCoords = emailCoordinatorsRepository.findHimachalCoordinators(department);
		if (himachalCoords != null) {
			return himachalCoords;
		} else {
			return new ArrayList<>();
		}
	}

	public List<String> fetchPunjabCoordinators(@NotEmpty String district) {
		List<String> punjabCoords = new ArrayList<>();
		List<PunjabCoordinators> punjabCoordinators = punjabCoordinatorsRepository.findByDistrict(district);
		if (punjabCoordinators != null && punjabCoordinators.size() > 0) {
			for (PunjabCoordinators punjabCoordinators2 : punjabCoordinators) {
				punjabCoords.add(punjabCoordinators2.getEmail());
			}
		}
		return punjabCoords;
	}

	public Set<String> fetchDAs(OrganizationBean organizationDetails) {
		List<EmailCoordinators> da = null;
		Set<String> finalDa = new HashSet<>();
		if (organizationDetails.getEmployment().equalsIgnoreCase("central")
				|| organizationDetails.getEmployment().equalsIgnoreCase("ut")) {
			da = emailCoordinatorsRepository.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(
					organizationDetails.getEmployment(), organizationDetails.getMinistry(),
					organizationDetails.getDepartment(), "a");
		} else if (organizationDetails.getEmployment().equalsIgnoreCase("state")) {
			da = emailCoordinatorsRepository.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(
					organizationDetails.getEmployment(), organizationDetails.getState(),
					organizationDetails.getDepartment(), "a");
		} else {
			da = emailCoordinatorsRepository.findByEmploymentCategoryAndMinistryAndStatus(
					organizationDetails.getEmployment(), organizationDetails.getMinistry(), "a");
		}
		if (da != null) {
			for (EmailCoordinators emailCoordinators : da) {
				if (emailCoordinators.getEmpType().equalsIgnoreCase("d")
						|| emailCoordinators.getEmpType().equalsIgnoreCase("dc")) {
					if (emailCoordinators.getEmail().equalsIgnoreCase(emailCoordinators.getAdminEmail())) {
						if (isSupportEmail(emailCoordinators.getEmail())) {
							continue;
						}
						finalDa.add(emailCoordinators.getEmail());
					}
				}
			}
		}
		return finalDa;
	}

	public Set<String> fetchCoordinators(OrganizationBean organizationDetails) {
		List<EmailCoordinators> da = null;
		Set<String> finalDa = new HashSet<>();

		if (organizationDetails.getEmployment().equalsIgnoreCase("central")
				|| organizationDetails.getEmployment().equalsIgnoreCase("ut")) {
			if (organizationDetails.getEmployment().equalsIgnoreCase("Central")) {
				if (!organizationDetails.getPostingState().equalsIgnoreCase("Delhi")) {
					if (organizationDetails.getPostingState().equalsIgnoreCase("Kerala")) {
						finalDa.add("r.abhilash@nic.in");
					} else if (organizationDetails.getPostingState().equalsIgnoreCase("Andaman and Nicobar Islands")) {
						finalDa.add("gana.tn@nic.in");
					} else if (organizationDetails.getPostingState().equalsIgnoreCase("Andhra Pradesh")) {
						finalDa.add("ramakanth@nic.in");
					} else if (organizationDetails.getPostingState().equalsIgnoreCase("Arunachal Pradesh")) {
						finalDa.add("opung.ering@nic.in");
					} else if (organizationDetails.getPostingState().equalsIgnoreCase("Jammu and Kashmir")) {
						finalDa.add("sudhir.sharma@nic.in");
					} else if (organizationDetails.getPostingState().equalsIgnoreCase("Karnataka")) {
						finalDa.add("sujatha.dpawar@nic.in");
					} else if (organizationDetails.getPostingState().equalsIgnoreCase("Maharashtra")) {
						if (organizationDetails.getCity().equalsIgnoreCase("Nagpur")) {
							finalDa.add("sdhoke@nic.in");
						} else if (organizationDetails.getCity().equalsIgnoreCase("Pune")) {
							finalDa.add("rm.kharse@nic.in");
						} else {
							finalDa.add("navare.sr@nic.in");
						}
					} else if (organizationDetails.getPostingState().equalsIgnoreCase("Puducherry")) {
						finalDa.add("raja.pon@nic.in");
					} else {
						da = emailCoordinatorsRepository.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(
								organizationDetails.getEmployment(), organizationDetails.getMinistry(),
								organizationDetails.getDepartment(), "a");

						if (da == null || da.size() == 0) {
							da = emailCoordinatorsRepository.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(
									"State", organizationDetails.getPostingState(), "other", "a");
						}
					}
				}else {
					da = emailCoordinatorsRepository.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(
							organizationDetails.getEmployment(), organizationDetails.getMinistry(),
							organizationDetails.getDepartment(), "a");

					if (da == null || da.size() == 0) {
						da = emailCoordinatorsRepository.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus("State",
								organizationDetails.getPostingState(), "other", "a");
					}
				}
			} else {
				da = emailCoordinatorsRepository.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(
						organizationDetails.getEmployment(), organizationDetails.getMinistry(),
						organizationDetails.getDepartment(), "a");
			}
		} else if (organizationDetails.getEmployment().equalsIgnoreCase("state")) {
			da = emailCoordinatorsRepository.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(
					organizationDetails.getEmployment(), organizationDetails.getState(),
					organizationDetails.getDepartment(), "a");
		} else {
			da = emailCoordinatorsRepository.findByEmploymentCategoryAndMinistryAndStatus(
					organizationDetails.getEmployment(), organizationDetails.getMinistry(), "a");
		}
		if (null != da) {
			for (EmailCoordinators emailCoordinators : da) {
				if (emailCoordinators.getEmpType() == null || emailCoordinators.getEmpType().isEmpty()
						|| emailCoordinators.getEmpType().equalsIgnoreCase("c")
						|| emailCoordinators.getEmpType().equalsIgnoreCase("dc")) {
					if (!emailCoordinators.getEmail().equalsIgnoreCase(emailCoordinators.getAdminEmail())) {
						if (isSupportEmail(emailCoordinators.getEmail())) {
							continue;
						}
						finalDa.add(emailCoordinators.getEmail());
					}
				}
			}
		}
		return finalDa;
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

	public List<Map<String, String>> fetchDAsOrCoordinators(@NotEmpty String bo) {
		Map<String, String> map = new HashMap<>();
		List<Map<String, String>> list = new ArrayList<>();
		List<EmailCoordinators> emailCoordinators = emailCoordinatorsRepository.findByBo(bo);
		if (emailCoordinators != null) {
			for (EmailCoordinators emailCoordinators2 : emailCoordinators) {
				if (!utilityService.isSupportEmail(emailCoordinators2.getEmail())) {
					if (emailCoordinators2.getEmail().equalsIgnoreCase(emailCoordinators2.getAdminEmail())) {
						map.put("daEmail", emailCoordinators2.getEmail());
					} else {
						map.put("coordinatorEmail", emailCoordinators2.getEmail());
					}
				}
				map.put("employmentCategory", emailCoordinators2.getEmploymentCategory());
				if (emailCoordinators2.getEmploymentCategory().equalsIgnoreCase("central")) {
					map.put("ministry", emailCoordinators2.getMinistry());
					map.put("department", emailCoordinators2.getDepartment());
				} else if (emailCoordinators2.getEmploymentCategory().equalsIgnoreCase("state")) {
					map.put("state", emailCoordinators2.getMinistry());
					map.put("department", emailCoordinators2.getDepartment());
				} else {
					map.put("organization", emailCoordinators2.getMinistry());
				}
				list.add(map);
			}
		}
		return list;
	}

	// by sunny
	public List<String> fetchDomains(String empCategory, String ministry, String empDept) {
		List<String> emailCoords = null;
		List<String> emailCoordinators = emailCoordinatorsRepository.findByDomain(empCategory, ministry, empDept);
		if (emailCoordinators != null && emailCoordinators.size() > 0) {
			emailCoords = new ArrayList<>();
			for (String temp : emailCoordinators) {
				emailCoords.add(temp);
			}
		}
		System.out.println(emailCoordinators);
		return emailCoords;
	}

	public List<String> fetchBOs(OrganizationBean organizationDetails) {
		if (organizationDetails.getEmployment().equalsIgnoreCase("central")
				|| organizationDetails.getEmployment().equalsIgnoreCase("state")) {
			return emailCoordinatorsRepository.findBOs(organizationDetails.getEmployment(),
					organizationDetails.getMinistry(), organizationDetails.getDepartment());
		} else {
			return emailCoordinatorsRepository.findBOs(organizationDetails.getEmployment(),
					organizationDetails.getMinistry());
		}
	}

	public List<String> fetchDomains1(String empCategory, String ministry) {
		List<String> emailCoords = null;
		List<String> emailCoordinators = emailCoordinatorsRepository.findByDomain1(empCategory, ministry);
		if (emailCoordinators != null && emailCoordinators.size() > 0) {
			emailCoords = new ArrayList<>();
			for (String temp : emailCoordinators) {
				emailCoords.add(temp);
			}
		}
		System.out.println(emailCoordinators);
		return emailCoords;
	}
//changes start by sunny
public List<String> fetchdistDomain() {
		List<String> emailCoords = null;
		List<String> emailCoordinators = emailCoordinatorsRepository.fetchdistDomain();
		if (emailCoordinators != null && emailCoordinators.size() > 0) {
			emailCoords = new ArrayList<>();
			for (String temp : emailCoordinators) {
				emailCoords.add(temp);
			}
		}
		System.out.println(emailCoordinators);
		return emailCoords;
	}
public List<String> fetchByEmploymentCategory(){
		List<String> emailCoords = null;
//		List<String> emailCoordinators = emailCoordinatorsRepository.fetchByEmploymentCategory();
//		if (emailCoordinators != null && emailCoordinators.size() > 0) {
//			emailCoords = new ArrayList<>();
//			for (String temp : emailCoordinators) {
//				emailCoords.add(temp);
//			}
//		}
//		System.out.println(emailCoordinators);
		return emailCoords;
	}
//	public List<String> fetchByCentralMinistry(String empCategory) {
//		List<String> emailCoords = null;
//		List<String> emailCoordinators = emailCoordinatorsRepository.fetchByCentralMinistry(empCategory);
//		if (emailCoordinators != null && emailCoordinators.size() > 0) {
//			emailCoords = new ArrayList<>();
//			for (String temp : emailCoordinators) {
//				emailCoords.add(temp);
//			}
//		}
//		System.out.println(emailCoordinators);
//		return emailCoords;
//	}
	public List<String> fetchByCentralDept(String ministry) {
		List<String> emailCoords = null;
//		List<String> emailCoordinators = emailCoordinatorsRepository.fetchByCentralDept(ministry);
//		if (emailCoordinators != null && emailCoordinators.size() > 0) {
//			emailCoords = new ArrayList<>();
//			for (String temp : emailCoordinators) {
//				emailCoords.add(temp);
//			}
//		}
//		System.out.println(emailCoordinators);
		return emailCoords;
	}
//changes end by sunny

}
