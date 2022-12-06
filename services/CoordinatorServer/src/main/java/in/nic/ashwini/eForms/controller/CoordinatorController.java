package in.nic.ashwini.eForms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.entities.OrganizationBean;
import in.nic.ashwini.eForms.services.CoordinatorService;
import models.OrganizationDto;

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
	
	@RequestMapping("/fetchDAsOrCoordinators")
	public List<Map<String, String>> fetchDAsOrCoordinators(@RequestParam @NotEmpty String bo) {
		return coordinatorService.fetchDAsOrCoordinators(bo);
	}
	//by start of sunny changes
	@RequestMapping("/fetchDomainsByCatAndMinAndDep")
	public List<String> fetchDomainsByCatAndMinAndDep(@RequestParam String empCategory,@RequestParam String ministry, @RequestParam String empDept) {
		return coordinatorService.fetchDomains(empCategory,ministry,empDept);
	}
	@RequestMapping("/fetchDomainsByCatAndMin")
	public List<String> fetchDomainsByCatAndMin(@RequestParam String empCategory,@RequestParam String ministry) {
		return coordinatorService.fetchDomains1(empCategory,ministry);
	}
	
	@RequestMapping("/fetchBOsAndCoordinatorsByCategoryMinistryAndDepartment")
	public Map<String, Object> fetchBOsAndCoordinatorsByCategoryMinistryAndDepartment(@RequestBody OrganizationBean organizationDetails) {
		Set<String> coordinators = coordinatorService.fetchCoordinators(organizationDetails);
		List<String> bos = coordinatorService.fetchBOs(organizationDetails);
		Map<String, Object> map = new HashMap<>();
		map.put("bos", bos);
		map.put("coordinators", coordinators);
		return map;
	}
	@RequestMapping("/fetchdistDomain")
	public List<String> fetchdistDomain() {
		return coordinatorService.fetchdistDomain();
	}
	@RequestMapping("/fetchByEmploymentCategory")
	public List<String> fetchByEmploymentCategory() {
		System.out.println("data of department:::"+coordinatorService.fetchByEmploymentCategory());
		return coordinatorService.fetchByEmploymentCategory();
	}
//	@RequestMapping("/fetchByCentralMinistry")
//	public List<String> fetchByCentralMinistry(@RequestParam String empCategory) {
//		return coordinatorService.fetchByCentralMinistry(empCategory);
//	}
	@RequestMapping("/fetchByCentralDept")
	public List<String> fetchByCentralDept(@RequestParam String ministry) {
		return coordinatorService.fetchByCentralDept(ministry);
	}
//end of sunny changes
	
}
