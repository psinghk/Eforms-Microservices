package in.nic.ashwini.eForms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.services.AdminService;

@RestController
public class AdminController {
	
	private final AdminService adminService;
	
	@Autowired
	public AdminController(AdminService adminService) {
		super();
		this.adminService = adminService;
	}

	@RequestMapping("/isUserSupport")
	public Boolean isUserSupport(@RequestParam @NotEmpty String remoteIp, @RequestParam @NotEmpty String email, @RequestParam @NotEmpty String mobile) {
		return adminService.isUserSupport(remoteIp, email, mobile);
	}
	
	@RequestMapping("/isUserAdmin")
	public Boolean isUserAdmin(@RequestParam @NotEmpty String remoteIp, @RequestParam @NotEmpty String email, @RequestParam @NotEmpty String mobile) {
		return adminService.isUserAdmin(remoteIp, email, mobile);
	}
	
	@RequestMapping("/isUserDashboardAdmin")
	public Boolean isUserDashboardAdmin(@RequestParam @NotEmpty String email) {
		return adminService.isUserDashboardAdmin(email);
	}
	
	@GetMapping("/fetchAllowedForms")
	public Set<String> fetchAllowedForms(@RequestParam @NotEmpty String email, @RequestParam String role) {
		return adminService.fetchAllowedForms(email, role);
	}
	
	@GetMapping("/fetchServices")
	public Map<String, Object> fetchServices(){
		Map<String, Object> data = new HashMap<>();
		data.put("internal", adminService.fetchInternalServices());
		data.put("external", adminService.fetchExternalServices());
		return data;
	}
	
	@GetMapping("/isRegNumberMatchesWithApiCall")
	public boolean isRegNumberMatchesWithApiCall(@RequestParam("regNumber") @NotEmpty String regNumber, HttpServletRequest request) {
		return adminService.isRegNumberMatchesWithApiCall(regNumber,request.getRequestURI());
	}
	
	@GetMapping("/fetchServiceName")
	public String fetchServiceName(@RequestParam("regNumber") @NotEmpty String regNumber) {
		return adminService.fetchServiceName(regNumber);
	}
}
