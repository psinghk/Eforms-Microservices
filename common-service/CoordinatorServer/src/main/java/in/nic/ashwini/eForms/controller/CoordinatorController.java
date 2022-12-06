package in.nic.ashwini.eForms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.db.master.entities.EmailCoordinators;
import in.nic.ashwini.eForms.db.master.entities.OrganizationBean;
import in.nic.ashwini.eForms.db.slave.entities.EmailCoordinatorsFromSlave;
import in.nic.ashwini.eForms.services.CoordinatorService;

@RestController
public class CoordinatorController {

	private final CoordinatorService coordinatorService;

	@Autowired
	public CoordinatorController(CoordinatorService coordinatorService) {
		super();
		this.coordinatorService = coordinatorService;
	}

	@GetMapping("/isUserCo")
	public Boolean isUserCo(@RequestParam @NotEmpty String ip, @RequestParam @NotEmpty String email) {
		return coordinatorService.isUserCo(ip, email);
	}

	@GetMapping("/isUserVpnCo")
	public Boolean isUserVpnCo(@RequestParam @NotEmpty String email) {
		return coordinatorService.isUserVpnCo(email);
	}

	@GetMapping("/fetchHimachalCoords")
	public List<String> fetchHimachalCoords(@RequestParam @NotEmpty String department) {
		return coordinatorService.fetchHimachalCoords(department);
	}

	@GetMapping("/fetchHimachalDa")
	public String fetchHimachalDa() {
		return "kaushal.shailender@nic.in";
	}

	@GetMapping("/fetchGemDAForFreeAccounts")
	public String fetchGemDAForFreeAccounts() {
		return "grm1-gem@gem.gov.in";
	}

	@GetMapping("/fetchGemDAForPaidAccounts")
	public String fetchGemDAForPaidAccounts() {
		return "lily.prasad@gem.gov.in";
	}

	@GetMapping("/fetchNdcPuneCoord")
	public String fetchNdcPuneCoord() {
		return "vaij.v@nic.in";
	}

	@GetMapping("/fetchPunjabCoordinators")
	public List<String> fetchPunjabCoordinators(@RequestParam @NotEmpty String district) {
		return coordinatorService.fetchPunjabCoordinators(district);
	}

	@PostMapping("/fetchDAs")
	public List<String> fetchDAs(@RequestBody OrganizationBean organizationDetails) {
		return new java.util.ArrayList<>(coordinatorService.fetchDAs(organizationDetails));
	}

	@PostMapping("/fetchCoordinators")
	public List<String> fetchCoordinators(@RequestBody OrganizationBean organizationDetails) {
		return new java.util.ArrayList<>(coordinatorService.fetchCoordinators(organizationDetails));
	}

	@GetMapping("/fetchDAsOrCoordinators")
	public List<Map<String, String>> fetchDAsOrCoordinators(@RequestParam @NotEmpty String bo) {
		return coordinatorService.fetchDAsOrCoordinators(bo);
	}

	// by sunny
	@GetMapping("/fetchDomainsByCatAndMinAndDep")
	public List<String> fetchDomainsByCatAndMinAndDep(@RequestParam String empCategory, @RequestParam String ministry,
			@RequestParam String empDept) {
		return coordinatorService.fetchDomains(empCategory, ministry, empDept);
	}

	@GetMapping("/fetchDomainsByCatAndMin")
	public List<String> fetchDomainsByCatAndMin(@RequestParam String empCategory, @RequestParam String ministry) {
		return coordinatorService.fetchDomains1(empCategory, ministry);
	}

	@PostMapping("/fetchBOsAndCoordinatorsByCategoryMinistryAndDepartment")
	public Map<String, Object> fetchBOsAndCoordinatorsByCategoryMinistryAndDepartment(
			@RequestBody OrganizationBean organizationDetails) {
		Set<String> coordinators = coordinatorService.fetchCoordinators(organizationDetails);
		List<String> bos = coordinatorService.fetchBOs(organizationDetails);
		Map<String, Object> map = new HashMap<>();
		map.put("bos", bos);
		map.put("coordinators", coordinators);
		return map;
	}

	// new added mappings

	@GetMapping("/findById")
	public EmailCoordinatorsFromSlave findById(@RequestParam("id") int id) {
		System.out.println("Entering category");
		EmailCoordinatorsFromSlave emailCoordinators = coordinatorService.findById(id);
		return emailCoordinators;
	}

	@PostMapping("/insertcoord")
	public EmailCoordinators savedata(@RequestBody EmailCoordinators emailcoord) {
		System.out.println("Entering category");
		EmailCoordinators emailCoordinators = coordinatorService.savedata(emailcoord);
		return emailCoordinators;
	}

	@GetMapping("/findByEmploymentCategoryAndMinistryAndStatus")
	public List<EmailCoordinatorsFromSlave> findByEmploymentCategoryAndMinistryAndStatus(
			@RequestParam("category") String category, @RequestParam("ministry") String ministry,
			@RequestParam("status") String status) {
		return coordinatorService.findByEmploymentCategoryAndMinistryAndStatus(category, ministry, status);
	}

	@GetMapping("/findByEmploymentCategoryAndMinistryAndDepartmentAndStatus")
	public List<EmailCoordinatorsFromSlave> findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(
			@RequestParam("category") String category, @RequestParam("ministry") String ministry,
			@RequestParam("department") String department, @RequestParam("status") String status) {
		return coordinatorService.findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(category, ministry,
				department, status);
	}

	@GetMapping("/findByEmploymentCategoryAndMinistryAndDepartmentAndEmailAndStatus")
	public List<EmailCoordinatorsFromSlave> findByEmploymentCategoryAndMinistryAndDepartmentAndEmailAndStatus(
			@RequestParam("coordEmail") String email, @RequestParam("category") String category,
			@RequestParam("ministry") String ministry, @RequestParam("department") String department,
			@RequestParam("status") String status) {
		return coordinatorService.findByEmploymentCategoryAndMinistryAndDepartmentAndEmailAndStatus(email, category,
				ministry, department, status);
	}

	@GetMapping("/findByEmploymentCategoryAndMinistryAndEmailAndStatus")
	public List<EmailCoordinatorsFromSlave> findByEmploymentCategoryAndMinistryAndEmailAndStatus(
			@RequestParam("coordEmail") String email, @RequestParam("category") String category,
			@RequestParam("ministry") String ministry, @RequestParam("status") String status) {
		return coordinatorService.findByEmploymentCategoryAndMinistryAndEmailAndStatus(email, category, ministry,
				status);
	}

	@GetMapping("/findByEmploymentCategoryAndMinistryAndStatusPageable")
	public Page<EmailCoordinatorsFromSlave> findByEmploymentCategoryAndMinistryAndStatusPageable(
			@RequestParam("category") String category, @RequestParam("ministry") String ministry,
			@RequestParam("status") String status, @RequestParam("pagenumber") int pagenumber,
			@RequestParam("pagesize") int pagesize) {
		System.out.println("pageable inserted");
		Pageable sortedByIdDesc = PageRequest.of(pagenumber, pagesize);
		Page<EmailCoordinatorsFromSlave> retval = coordinatorService
				.findByEmploymentCategoryAndMinistryAndStatusPageable(category, ministry, status, sortedByIdDesc);
		return retval;
	}

	/* Commented for http post testing */

//	@PostMapping("/updateEmploymentCoordinator")
//	public int updateEmploymentCoordinator(@RequestParam("new_dept") String emp_new_dept,
//			@RequestParam("category") String emp_category, @RequestParam("ministry") String emp_min_state_org,
//			@RequestParam("department") String emp_dept) {
//
//		return coordinatorService.updateEmploymentCoordinator(emp_new_dept, emp_category, emp_min_state_org, emp_dept);
//
//	}

	@PostMapping("/updateEmploymentCoordinator")
	public int updateEmploymentCoordinator(@RequestBody EmailCoordinators emailcoord) {
		String emp_new_dept = emailcoord.getNewdept();
		String emp_category = emailcoord.getEmploymentCategory();
		String emp_min_state_org = emailcoord.getMinistry();
		String emp_dept = emailcoord.getDepartment();
		return coordinatorService.updateEmploymentCoordinator(emp_new_dept, emp_category, emp_min_state_org, emp_dept);

	}

	@GetMapping("/fetchList1")
	public Page<EmailCoordinatorsFromSlave> fetchList1(@RequestParam("pagenumber") int pagenumber,
			@RequestParam("pagesize") int pagesize, Pageable pageable,
			@RequestParam(name = "orgType", required = false, defaultValue = "") String orgType,
			@RequestParam(name = "minType", required = false, defaultValue = "") String minType,
			@RequestParam(name = "depType", required = false, defaultValue = "") String depType,
			@RequestParam(name = "search", required = false, defaultValue = "") String search) {

		Pageable sortedByIdDesc = PageRequest.of(pagenumber, pagesize);

		return coordinatorService.fetchList(sortedByIdDesc, orgType, minType, depType, search);
	}
}
