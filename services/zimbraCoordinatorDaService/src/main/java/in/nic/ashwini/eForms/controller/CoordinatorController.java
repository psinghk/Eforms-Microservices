package in.nic.ashwini.eForms.controller;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.entities.OrganizationBean;
import in.nic.ashwini.eForms.services.CoordinatorService;

@RestController
public class CoordinatorController {

	private final CoordinatorService coordinatorService;

	@Autowired
	public CoordinatorController(CoordinatorService coordinatorService) {
		super();
		this.coordinatorService = coordinatorService;
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
	//by sunny
	@RequestMapping("/fetchDomainsByCatAndMinAndDep")
	public List<String> fetchDomainsByCatAndMinAndDep(@RequestParam String empCategory,@RequestParam String ministry, @RequestParam String empDept) {
		return coordinatorService.fetchDomains(empCategory,ministry,empDept);
	}
	@RequestMapping("/fetchDomainsByCatAndMin")
	public List<String> fetchDomainsByCatAndMin(@RequestParam String empCategory,@RequestParam String ministry) {
		return coordinatorService.fetchDomains1(empCategory,ministry);
	}
	
	@RequestMapping("/fetchOrganizationDetails")
	public List<String> fetchEmploymentCategories(@RequestParam(defaultValue = "empty") String empCategory,@RequestParam(defaultValue = "empty") String ministry) {
		if(empCategory.equalsIgnoreCase("empty")) {
			return coordinatorService.fetchEmploymentCategories();
		}
		
		if(ministry.equalsIgnoreCase("empty")) {
			return coordinatorService.fetchMinistriesOrStatesOrOrganizations(empCategory);
		}
		
		return coordinatorService.fetchDepartments(empCategory,ministry);
	}
	
}
