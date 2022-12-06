package in.nic.eform.Profile.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.eform.Profile.exception.custom.NoRecordFoundException;
import in.nic.eform.Profile.service.MasterDataService;


// As we have have lots of services. So to segregate those there should be a controller level request mapping
@RestController
public class MasterDataController {

	
	// configure logger at class level
	
	
	@Autowired
	MasterDataService masterDataService;

	@GetMapping("/orgnization-ministries/{organization}")
	public ResponseEntity<List<String>> getOrganizationMinistryList(
			@RequestParam(value = "organization") String organization) throws NoRecordFoundException {
		return ResponseEntity.ok().body(masterDataService.getMinistryList(organization));
	}

	@GetMapping("/orgnization-ministry-departmetnts/{ministryId}")
	public ResponseEntity<List<String>> getOrganizationMininstryBasedDepartment(
			@RequestParam(value = "ministryId") String ministryId) throws NoRecordFoundException {
		return ResponseEntity.ok().body(masterDataService.getOrganizationMininstryBasedDepartments(ministryId));
	}

	@GetMapping("/states")
	public ResponseEntity<List<String>> getStates() throws NoRecordFoundException {
		return ResponseEntity.ok().body(masterDataService.getStateList());
	}

	@GetMapping("/districts/{stateName}")
	public ResponseEntity<List<String>> getDistrict(@RequestParam(value = "stateName") String stateName)
			throws NoRecordFoundException {
		return ResponseEntity.ok().body(masterDataService.getDistrictList(stateName));
	}

}
