package in.nic.ashwini.eForms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.services.AdminService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AdminController {
	
	private final AdminService adminService;
	
	@Autowired
	public AdminController(AdminService adminService) {
		super();
		this.adminService = adminService;
	}

	@GetMapping("/isUserSupport")
	public Boolean isUserSupport(@RequestParam @NotEmpty String remoteIp, @RequestParam @NotEmpty String email, @RequestParam @NotEmpty String mobile) {
		log.debug("Calling isUserSupport API with email : {} , mobile : {}, IP : {} ", email,mobile,remoteIp);
		return adminService.isUserSupport(remoteIp, email, mobile);
	}
	
	@GetMapping("/isUserAdmin")
	public Boolean isUserAdmin(@RequestParam @NotEmpty String remoteIp, @RequestParam @NotEmpty String email, @RequestParam @NotEmpty String mobile) {
		log.debug("Calling isUserAdmin API with email : {} , mobile : {}, IP : {} ", email,mobile,remoteIp);
		return adminService.isUserAdmin(remoteIp, email, mobile);
	}
	
	@GetMapping("/isUserDashboardAdmin")
	public Boolean isUserDashboardAdmin(@RequestParam @NotEmpty String email) {
		log.debug("Calling isUserDashboardAdmin API with email : {}", email);
		return adminService.isUserDashboardAdmin(email);
	}
	
	@GetMapping("/fetchAllowedForms")
	public Set<String> fetchAllowedForms(@RequestParam @NotEmpty String email, @RequestParam String role) {
		log.debug("Calling fetchAllowedForms API with email : {} and role : {}", email,role);
		return adminService.fetchAllowedForms(email, role);
	}
	
	@GetMapping("/fetchServices")
	public Map<String, Object> fetchServices(){
		log.debug("Calling fetchServices API");
		Map<String, Object> data = new HashMap<>();
		data.put("internal", adminService.fetchInternalServices());
		data.put("external", adminService.fetchExternalServices());
		return data;
	}
	
	@GetMapping("/isRegNumberMatchesWithApiCall")
	public boolean isRegNumberMatchesWithApiCall(@RequestParam("regNumber") @NotEmpty String regNumber, HttpServletRequest request) {
		log.debug("Calling isRegNumberMatchesWithApiCall API with registration number : {}", regNumber);
		return adminService.isRegNumberMatchesWithApiCall(regNumber,request.getRequestURI());
	}
	
	@GetMapping("/fetchServiceName")
	public String fetchServiceName(@RequestParam("regNumber") @NotEmpty String regNumber) {
		log.debug("Calling fetchServiceName API with regNumber : {}", regNumber);
		return adminService.fetchServiceName(regNumber);
	}
	
	
	
	@GetMapping("/fetchMailAdmins")
	public List<String> fetchMailAdmins(@RequestParam("field") @NotEmpty String field) {
		log.debug("Calling fetchMailAdmins API with field : {}", field);
		return adminService.fetchMailAdmins(field);
	}
}
