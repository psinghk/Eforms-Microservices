package in.nic.ashwini.eForms.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.db.master.entities.EmailCoordinators;
import in.nic.ashwini.eForms.db.master.entities.OrganizationBean;
import in.nic.ashwini.eForms.db.master.repositories.EmailCoordinatorsRepository;
import in.nic.ashwini.eForms.db.master.repositories.EmailCoordinatorsRepositoryPageable;
import in.nic.ashwini.eForms.db.master.repositories.PunjabCoordinatorsRepository;
import in.nic.ashwini.eForms.db.master.repositories.VpnCoordinatorsRepository;
import in.nic.ashwini.eForms.db.slave.entities.EmailCoordinatorsFromSlave;
import in.nic.ashwini.eForms.db.slave.entities.PunjabCoordinatorsFromSlave;
import in.nic.ashwini.eForms.db.slave.entities.VpnCoordinatorsFromSlave;
import in.nic.ashwini.eForms.db.slave.repositories.EmailCoordinatorsRepositoryPageableToRead;
import in.nic.ashwini.eForms.db.slave.repositories.EmailCoordinatorsRepositoryToRead;
import in.nic.ashwini.eForms.db.slave.repositories.PunjabCoordinatorsRepositoryToRead;
import in.nic.ashwini.eForms.db.slave.repositories.VpnCoordinatorsRepositoryToRead;
import models.MobileAndName;

@Service
public class CoordinatorService {

	private final EmailCoordinatorsRepository emailCoordinatorsRepository;
	private final VpnCoordinatorsRepository vpnCoordinatorsRepository;
	private final PunjabCoordinatorsRepository punjabCoordinatorsRepository;
	private final UtilityService utilityService;
	private final EmailCoordinatorsRepositoryPageable emailCoordinatorsRepositoryPageable;
	private final EmailCoordinatorsRepositoryToRead emailCoordinatorsRepositoryToRead;
	private final VpnCoordinatorsRepositoryToRead vpnCoordinatorsRepositoryToRead;
	private final PunjabCoordinatorsRepositoryToRead punjabCoordinatorsRepositoryToRead;
	private final EmailCoordinatorsRepositoryPageableToRead emailCoordinatorsRepositoryPageableToRead;

	@Autowired
	public CoordinatorService(EmailCoordinatorsRepository emailCoordinatorsRepository,
			VpnCoordinatorsRepository vpnCoordinatorsRepository, UtilityService utilityService,
			PunjabCoordinatorsRepository punjabCoordinatorsRepository,
			EmailCoordinatorsRepositoryPageable emailCoordinatorsRepositoryPageable,
			EmailCoordinatorsRepositoryToRead emailCoordinatorsRepositoryToRead,
			VpnCoordinatorsRepositoryToRead vpnCoordinatorsRepositoryToRead,
			PunjabCoordinatorsRepositoryToRead punjabCoordinatorsRepositoryToRead,
			EmailCoordinatorsRepositoryPageableToRead emailCoordinatorsRepositoryPageableToRead) {
		super();
		this.emailCoordinatorsRepository = emailCoordinatorsRepository;
		this.vpnCoordinatorsRepository = vpnCoordinatorsRepository;
		this.utilityService = utilityService;
		this.punjabCoordinatorsRepository = punjabCoordinatorsRepository;
		this.emailCoordinatorsRepositoryPageable = emailCoordinatorsRepositoryPageable;
		this.emailCoordinatorsRepositoryToRead = emailCoordinatorsRepositoryToRead;
		this.vpnCoordinatorsRepositoryToRead = vpnCoordinatorsRepositoryToRead;
		this.punjabCoordinatorsRepositoryToRead = punjabCoordinatorsRepositoryToRead;
		this.emailCoordinatorsRepositoryPageableToRead = emailCoordinatorsRepositoryPageableToRead;
	}

	public boolean isUserCo(String ip, String email) {
		Set<String> aliases = utilityService.fetchAliasesFromLdap(email);
		for (String mail : aliases) {
			List<EmailCoordinatorsFromSlave> co = emailCoordinatorsRepositoryToRead
					.findByEmailAndStatusAndVpnIpContaining(mail, "a", ip);
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
			Optional<VpnCoordinatorsFromSlave> vpnCo = vpnCoordinatorsRepositoryToRead.findFirstByEmailAndStatus(sMail,
					"a");
			if (vpnCo.isPresent()) {
				return true;
			}
		}
		return false;
	}

	public List<String> fetchHimachalCoords(@NotEmpty String department) {
		List<String> himachalCoords = emailCoordinatorsRepositoryToRead.findHimachalCoordinators(department);
		if (himachalCoords != null) {
			return himachalCoords;
		} else {
			return new ArrayList<>();
		}
	}

	public List<String> fetchPunjabCoordinators(@NotEmpty String district) {
		List<String> punjabCoords = new ArrayList<>();
		List<PunjabCoordinatorsFromSlave> punjabCoordinators = punjabCoordinatorsRepositoryToRead
				.findByDistrict(district);
		if (punjabCoordinators != null && punjabCoordinators.size() > 0) {
			for (PunjabCoordinatorsFromSlave punjabCoordinators2 : punjabCoordinators) {
				punjabCoords.add(punjabCoordinators2.getEmail());
			}
		}
		return punjabCoords;
	}

	public Set<String> fetchDAs(OrganizationBean organizationDetails) {
		List<EmailCoordinatorsFromSlave> da = null;
		Set<String> finalDa = new HashSet<>();
		if (organizationDetails.getEmployment().equalsIgnoreCase("central")
				|| organizationDetails.getEmployment().equalsIgnoreCase("ut")) {
			da = emailCoordinatorsRepositoryToRead.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(
					organizationDetails.getEmployment(), organizationDetails.getMinistry(),
					organizationDetails.getDepartment(), "a");
		} else if (organizationDetails.getEmployment().equalsIgnoreCase("state")) {
			da = emailCoordinatorsRepositoryToRead.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(
					organizationDetails.getEmployment(), organizationDetails.getState(),
					organizationDetails.getDepartment(), "a");
		} else {
			da = emailCoordinatorsRepositoryToRead.findByEmploymentCategoryAndMinistryAndStatus(
					organizationDetails.getEmployment(), organizationDetails.getMinistry(), "a");
		}
		if (da != null) {
			for (EmailCoordinatorsFromSlave emailCoordinators : da) {
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
		List<EmailCoordinatorsFromSlave> da = null;
		Set<String> finalDa = new HashSet<>();

		if (organizationDetails.getEmployment().equalsIgnoreCase("central")
				|| organizationDetails.getEmployment().equalsIgnoreCase("ut")) {
			if (organizationDetails.getEmployment().equalsIgnoreCase("Central")
					&& (organizationDetails.getPostingState() != null
							&& !organizationDetails.getPostingState().equalsIgnoreCase("Delhi"))) {
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
					da = emailCoordinatorsRepositoryToRead.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(
							organizationDetails.getEmployment(), organizationDetails.getMinistry(),
							organizationDetails.getDepartment(), "a");

					if (da == null || da.size() == 0) {
						da = emailCoordinatorsRepositoryToRead
								.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus("State",
										organizationDetails.getPostingState(), "other", "a");
					}
				}
			} else {
				da = emailCoordinatorsRepositoryToRead.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(
						organizationDetails.getEmployment(), organizationDetails.getMinistry(),
						organizationDetails.getDepartment(), "a");

				if (da == null || da.size() == 0) {
					if (organizationDetails.getPostingState() != null) {
						da = emailCoordinatorsRepositoryToRead
								.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus("State",
										organizationDetails.getPostingState(), "other", "a");
					} else {
						da = new ArrayList<>();
					}
				}
			}
		} else if (organizationDetails.getEmployment().equalsIgnoreCase("state")) {
			da = emailCoordinatorsRepositoryToRead.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(
					organizationDetails.getEmployment(), organizationDetails.getState(),
					organizationDetails.getDepartment(), "a");
		} else {
			da = emailCoordinatorsRepositoryToRead.findByEmploymentCategoryAndMinistryAndStatus(
					organizationDetails.getEmployment(), organizationDetails.getMinistry(), "a");
		}
		if (null != da) {
			for (EmailCoordinatorsFromSlave emailCoordinators : da) {
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
		List<EmailCoordinatorsFromSlave> emailCoordinators = emailCoordinatorsRepositoryToRead.findByBo(bo);
		if (emailCoordinators != null) {
			for (EmailCoordinatorsFromSlave emailCoordinators2 : emailCoordinators) {
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
		List<String> emailCoordinators = emailCoordinatorsRepositoryToRead.findByDomain(empCategory, ministry, empDept);
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
			return emailCoordinatorsRepositoryToRead.findBOs(organizationDetails.getEmployment(),
					organizationDetails.getMinistry(), organizationDetails.getDepartment());
		} else {
			return emailCoordinatorsRepositoryToRead.findBOs(organizationDetails.getEmployment(),
					organizationDetails.getMinistry());
		}
	}

	public List<String> fetchDomains1(String empCategory, String ministry) {
		List<String> emailCoords = null;
		List<String> emailCoordinators = emailCoordinatorsRepositoryToRead.findByDomain1(empCategory, ministry);
		if (emailCoordinators != null && emailCoordinators.size() > 0) {
			emailCoords = new ArrayList<>();
			for (String temp : emailCoordinators) {
				emailCoords.add(temp);
			}
		}
		System.out.println(emailCoordinators);
		return emailCoords;
	}

	// new Added mappings
	public EmailCoordinatorsFromSlave findById(int id) {
		Optional<EmailCoordinatorsFromSlave> retval = emailCoordinatorsRepositoryToRead.findById(id);
		EmailCoordinatorsFromSlave emailcoord = retval.get();
		return emailcoord;
	}

	public EmailCoordinators savedata(EmailCoordinators emailcoord) {
		LocalDateTime currentTime = LocalDateTime.now();
		emailcoord.setCreatedOn(currentTime);
		emailcoord.setAdminEmail("support@gov.in");
		EmailCoordinators retval = emailCoordinatorsRepository.save(emailcoord);
		return retval;
	}

	public List<EmailCoordinatorsFromSlave> findByEmploymentCategoryAndMinistryAndStatus(String category,
			String ministry, String status) {
		List<EmailCoordinatorsFromSlave> retval = emailCoordinatorsRepositoryToRead
				.findByEmploymentCategoryAndMinistryAndStatus(category, ministry, status);
		List<EmailCoordinatorsFromSlave> copyRetval = new ArrayList<>();
		for (EmailCoordinatorsFromSlave emailCoordinatorsFromSlave : retval) {
			if (ValidationService.isFormatValid("email", emailCoordinatorsFromSlave.getEmail())) {
				if (emailCoordinatorsFromSlave.getName() == null) {
					MobileAndName mobileAndName = utilityService
							.fetchMobileAndName(emailCoordinatorsFromSlave.getEmail());
					if(mobileAndName != null) {
						emailCoordinatorsFromSlave.setName(mobileAndName.getCn() != null ? mobileAndName.getCn() : "");
						emailCoordinatorsFromSlave.setMobile(mobileAndName.getMobile() !=null ? mobileAndName.getMobile() : "");
					}
				} else if (emailCoordinatorsFromSlave.getName().equals("null")
						|| emailCoordinatorsFromSlave.getName().isEmpty()) {
					MobileAndName mobileAndName = utilityService
							.fetchMobileAndName(emailCoordinatorsFromSlave.getEmail());
					if(mobileAndName != null) {
						emailCoordinatorsFromSlave.setName(mobileAndName.getCn() != null ? mobileAndName.getCn() : "");
						emailCoordinatorsFromSlave.setMobile(mobileAndName.getMobile() !=null ? mobileAndName.getMobile() : "");
					}
				}
				copyRetval.add(emailCoordinatorsFromSlave);
			}
		}
		return copyRetval;
	}

	public Page<EmailCoordinatorsFromSlave> findByEmploymentCategoryAndMinistryAndStatusPageable(String category,
			String ministry, String status, Pageable pageable) {
		Page<EmailCoordinatorsFromSlave> retval = emailCoordinatorsRepositoryPageableToRead
				.findByEmploymentCategoryAndMinistryAndStatus(category, ministry, status, pageable);
		return retval;
	}

	public int updateEmploymentCoordinator(String emp_new_dept, String emp_category, String emp_min_state_org,
			String emp_dept) {
		int retval = 0;
		List<EmailCoordinators> listtobeUpdated = null;
		listtobeUpdated = emailCoordinatorsRepository.findByEmploymentCategoryAndMinistryAndDepartment(emp_category,
				emp_min_state_org, emp_dept);

		if (!listtobeUpdated.isEmpty()) {
			for (EmailCoordinators ecoord : listtobeUpdated) {
				ecoord.setDepartment(emp_new_dept);
				LocalDateTime currentTime = LocalDateTime.now();
				ecoord.setCreatedOn(currentTime);
				emailCoordinatorsRepository.save(ecoord);
			}
			retval = 1;
		}
		return retval;
	}

	public Page<EmailCoordinatorsFromSlave> fetchList(Pageable sortedByIdDesc, String orgType, String minType,
			String depType, String search) {

		Page<EmailCoordinatorsFromSlave> list = null;

		if (orgType.isEmpty() && minType.isEmpty() && depType.isEmpty()) {

			if (search.isEmpty()) {
				list = emailCoordinatorsRepositoryToRead.findAll(sortedByIdDesc);
				System.out.println("LIST-ALL:::::::::::" + list);
			} else {
				list = emailCoordinatorsRepositoryToRead.findSearchData(search, sortedByIdDesc);
				System.out.println("LIST-Search:::::::::::" + list);
			}
		}

		if (!orgType.isEmpty() && minType.isEmpty() && depType.isEmpty()) {
			if (search.isEmpty()) {
				list = emailCoordinatorsRepositoryToRead.findByEmploymentCategory(orgType, sortedByIdDesc);
				System.out.println("LIST-orgtype:::::::::::" + list);
			} else {
				list = emailCoordinatorsRepositoryToRead.findSearchOrgData(orgType, search, sortedByIdDesc);
			}
		}

		if (!minType.isEmpty() && !orgType.isEmpty() && depType.isEmpty()) {
			if (search.isEmpty()) {

				list = emailCoordinatorsRepositoryToRead.findByOrgTypeAndMinType(orgType, minType, sortedByIdDesc);
				System.out.println("LIST-ministry:::::::::::" + list);
			} else {
				list = emailCoordinatorsRepositoryToRead.findSearchMinOrgData(orgType, minType, search, sortedByIdDesc);
			}
		}

		if (!depType.isEmpty() && !minType.isEmpty() && !orgType.isEmpty()) {

			if (search.isEmpty()) {

				list = emailCoordinatorsRepositoryToRead.fetchFinalList(orgType, minType, depType, sortedByIdDesc);
				System.out.println("LIST-department:::::::::::" + list);
			} else {
				list = emailCoordinatorsRepositoryToRead.findSearchMinOrgDeptData(orgType, minType, depType, search,
						sortedByIdDesc);
			}
		}
		System.out.println("LIST-ELEMENTS::::" + list.getNumberOfElements());
		System.out.println("Total number of count ::::::" + list.getTotalElements());
		return list;
	}

	public List<EmailCoordinatorsFromSlave> findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(String category,
			String ministry, String department, String status) {
		List<EmailCoordinatorsFromSlave> retval = emailCoordinatorsRepositoryToRead
				.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(category, ministry, department, status);
		List<EmailCoordinatorsFromSlave> copyRetval = new ArrayList<>();
		for (EmailCoordinatorsFromSlave emailCoordinatorsFromSlave : retval) {
			if (ValidationService.isFormatValid("email", emailCoordinatorsFromSlave.getEmail())) {
				if (emailCoordinatorsFromSlave.getName() == null) {
					MobileAndName mobileAndName = utilityService
							.fetchMobileAndName(emailCoordinatorsFromSlave.getEmail());
					emailCoordinatorsFromSlave.setName(mobileAndName.getCn());
					emailCoordinatorsFromSlave.setMobile(mobileAndName.getMobile());
				} else if (emailCoordinatorsFromSlave.getName().equals("null")
						|| emailCoordinatorsFromSlave.getName().isEmpty()) {
					MobileAndName mobileAndName = utilityService
							.fetchMobileAndName(emailCoordinatorsFromSlave.getEmail());
					emailCoordinatorsFromSlave.setName(mobileAndName.getCn());
					emailCoordinatorsFromSlave.setMobile(mobileAndName.getMobile());
				}
				copyRetval.add(emailCoordinatorsFromSlave);
			}
		}
		return copyRetval;
	}

	public List<EmailCoordinatorsFromSlave> findByEmploymentCategoryAndMinistryAndDepartmentAndEmailAndStatus(
			String email, String category, String ministry, String department, String status) {
		List<EmailCoordinatorsFromSlave> retval = emailCoordinatorsRepositoryToRead
				.findByEmploymentCategoryAndMinistryAndDepartmentAndEmailAndStatus(category, ministry,
						department, email, status);
		List<EmailCoordinatorsFromSlave> copyRetval = new ArrayList<>();
		for (EmailCoordinatorsFromSlave emailCoordinatorsFromSlave : retval) {
			if (ValidationService.isFormatValid("email", emailCoordinatorsFromSlave.getEmail())) {
				if (emailCoordinatorsFromSlave.getName() == null) {
					MobileAndName mobileAndName = utilityService
							.fetchMobileAndName(emailCoordinatorsFromSlave.getEmail());
					emailCoordinatorsFromSlave.setName(mobileAndName.getCn());
					emailCoordinatorsFromSlave.setMobile(mobileAndName.getMobile());
				} else if (emailCoordinatorsFromSlave.getName().equals("null")
						|| emailCoordinatorsFromSlave.getName().isEmpty()) {
					MobileAndName mobileAndName = utilityService
							.fetchMobileAndName(emailCoordinatorsFromSlave.getEmail());
					emailCoordinatorsFromSlave.setName(mobileAndName.getCn());
					emailCoordinatorsFromSlave.setMobile(mobileAndName.getMobile());
				}
				copyRetval.add(emailCoordinatorsFromSlave);
			}
		}
		return copyRetval;
	}

	public List<EmailCoordinatorsFromSlave> findByEmploymentCategoryAndMinistryAndEmailAndStatus(String email,
			String category, String ministry, String status) {
		List<EmailCoordinatorsFromSlave> retval = emailCoordinatorsRepositoryToRead
				.findByEmploymentCategoryAndMinistryAndEmailAndStatus(category, ministry, email, status);
		List<EmailCoordinatorsFromSlave> copyRetval = new ArrayList<>();
		for (EmailCoordinatorsFromSlave emailCoordinatorsFromSlave : retval) {
			if (ValidationService.isFormatValid("email", emailCoordinatorsFromSlave.getEmail())) {
				if (emailCoordinatorsFromSlave.getName() == null) {
					MobileAndName mobileAndName = utilityService
							.fetchMobileAndName(emailCoordinatorsFromSlave.getEmail());
					emailCoordinatorsFromSlave.setName(mobileAndName.getCn());
					emailCoordinatorsFromSlave.setMobile(mobileAndName.getMobile());
				} else if (emailCoordinatorsFromSlave.getName().equals("null")
						|| emailCoordinatorsFromSlave.getName().isEmpty()) {
					MobileAndName mobileAndName = utilityService
							.fetchMobileAndName(emailCoordinatorsFromSlave.getEmail());
					emailCoordinatorsFromSlave.setName(mobileAndName.getCn());
					emailCoordinatorsFromSlave.setMobile(mobileAndName.getMobile());
				}
				copyRetval.add(emailCoordinatorsFromSlave);
			}
		}
		return copyRetval;
	}
}
