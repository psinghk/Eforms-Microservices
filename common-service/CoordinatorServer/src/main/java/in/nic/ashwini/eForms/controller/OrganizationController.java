package in.nic.ashwini.eForms.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.exceptions.custom.NoRecordFoundException;
import in.nic.ashwini.eForms.services.OrganizationService;
import models.OrganizationDto;

@RestController
@RequestMapping("/organization")
public class OrganizationController {

	private final OrganizationService organizationService;

	@Autowired
	public OrganizationController(OrganizationService organizationService) {
		super();
		this.organizationService = organizationService;
	}

	@GetMapping("/employment")
	public Map<String, List<?>> fetchEmploymentCategory() throws NoRecordFoundException {
		Map<String, List<?>> employment = new HashMap<>();
		employment.put("employment", organizationService.empCategory());
		return employment;
	}

	@GetMapping("/ministries")
	public Map<String, List<?>> fetchMinistries(@RequestParam(value = "empCategory") String organization)
			throws NoRecordFoundException {
		Map<String, List<?>> ministries = new HashMap<>();
		try {
			ministries.put("ministries", organizationService
					.findMinistriesByEmpCategory(URLDecoder.decode(organization, StandardCharsets.UTF_8.toString())));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoRecordFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ministries;
	}

	@GetMapping("/ministry/departments")
	public Map<String, List<?>> getOrganizationMininstryBasedDepartment(@RequestParam String ministry) {
		Map<String, List<?>> departments = new HashMap<>();
		try {
			departments.put("departments", organizationService
					.findDepartmentsByMinistry(URLDecoder.decode(ministry, StandardCharsets.UTF_8.toString())));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoRecordFoundException e) {
			e.printStackTrace();
			if (departments.size() == 0)
				return null;
		}
		return departments;
	}

	@GetMapping("/states")
	public Map<String, List<String>> getStates() throws NoRecordFoundException {
		Map<String, List<String>> states = new HashMap<>();
		states.put("states", organizationService.findStates());
		return states;
	}

	@GetMapping("/state/districts")
	public Map<String, List<String>> getDistrict(@RequestParam(value = "state") String stateName)
			throws NoRecordFoundException {
		Map<String, List<String>> districts = new HashMap<>();
		try {
			districts.put("districts", organizationService
					.findDistrictsByState(URLDecoder.decode(stateName, StandardCharsets.UTF_8.toString())));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoRecordFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return districts;
	}

	@PostMapping(value = "/validateOrganization")
	public String validateOrganization(@Valid @RequestBody OrganizationDto organization) {
		return "";
	}
}
