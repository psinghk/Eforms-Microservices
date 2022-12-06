package in.nic.ashwini.eForms.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.entities.EmailCoordinators;
import in.nic.ashwini.eForms.entities.OrganizationBean;
import in.nic.ashwini.eForms.repositories.EmailCoordinatorsRepository;

@Service
public class CoordinatorService {

	private final EmailCoordinatorsRepository emailCoordinatorsRepository;
	private final UtilityService utilityService;

	@Autowired
	public CoordinatorService(EmailCoordinatorsRepository emailCoordinatorsRepository, UtilityService utilityService) {
		super();
		this.emailCoordinatorsRepository = emailCoordinatorsRepository;
		this.utilityService = utilityService;
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
			if (organizationDetails.getEmployment().equalsIgnoreCase("Central")
					&& (!organizationDetails.getPostingState().equalsIgnoreCase("Delhi"))) {
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
		
		List<Map<String, String>> list = new ArrayList<>();
		List<EmailCoordinators> emailCoordinators = emailCoordinatorsRepository.findByBo(bo);
		List<String> combination = new ArrayList<>();
		String value = "";
		if (emailCoordinators != null) {
			
			for (EmailCoordinators emailCoordinators2 : emailCoordinators) {
				Map<String, String> map = new HashMap<>();
				if (!utilityService.isSupportEmail(emailCoordinators2.getEmail())) {
					if (emailCoordinators2.getEmail().equalsIgnoreCase(emailCoordinators2.getAdminEmail())) {
						map.put("daEmail", emailCoordinators2.getEmail());
					} else {
						map.put("coordinatorEmail", emailCoordinators2.getEmail());
					}
					map.put("employmentCategory", emailCoordinators2.getEmploymentCategory());
					if (emailCoordinators2.getEmploymentCategory().equalsIgnoreCase("central")) {
						map.put("ministry", emailCoordinators2.getMinistry());
						map.put("department", emailCoordinators2.getDepartment());
						if (map.get("daEmail") != null) {
							value = map.get("employmentCategory") + "|" + map.get("ministry") + "|"
									+ map.get("department") + "|" + map.get("daEmail");
						} else {
							value = map.get("employmentCategory") + "|" + map.get("ministry") + "|"
									+ map.get("department") + "|" + map.get("coordinatorEmail");
						}

						if (combination.contains(value)) {
							continue;
						} else {
							combination.add(value);
						}
					} else if (emailCoordinators2.getEmploymentCategory().equalsIgnoreCase("state")) {
						map.put("state", emailCoordinators2.getMinistry());
						map.put("department", emailCoordinators2.getDepartment());
						if (map.get("daEmail") != null) {
							value = map.get("employmentCategory") + "|" + map.get("state") + "|" + map.get("department")
									+ "|" + map.get("daEmail");
						} else {
							value = map.get("employmentCategory") + "|" + map.get("state") + "|" + map.get("department")
									+ "|" + map.get("coordinatorEmail");
						}

						if (combination.contains(value)) {
							continue;
						} else {
							combination.add(value);
						}
					} else {
						map.put("organization", emailCoordinators2.getMinistry());

						if (map.get("daEmail") != null) {
							value = map.get("employmentCategory") + "|" + map.get("organization") + "|"
									+ map.get("daEmail");
						} else {
							value = map.get("employmentCategory") + "|" + map.get("organization") + "|"
									+ map.get("coordinatorEmail");
						}
						
						if (combination.contains(value)) {
							continue;
						} else {
							combination.add(value);
						}
					}
					list.add(map);
				}

				
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

	public List<String> fetchEmploymentCategories() {
		return emailCoordinatorsRepository.findDinstinctCategories();
	}

	public List<String> fetchMinistriesOrStatesOrOrganizations(String empCategory) {
		return emailCoordinatorsRepository.findMinistriesByEmpCategory(empCategory);
	}

	public List<String> fetchDepartments(String empCategory, String ministry) {
		return emailCoordinatorsRepository.findDepartmentsByMinistryAndCategory(empCategory, ministry);
	}

}
