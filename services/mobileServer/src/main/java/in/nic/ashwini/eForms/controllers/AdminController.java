package in.nic.ashwini.eForms.controllers;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.services.MobileServiceAdmin;


@RequestMapping("/admin")
@Validated
@RestController
public class AdminController {

	private final MobileServiceAdmin mobileServiceAdmin;
	private final ResponseBean responseBean;
	

	@Autowired
	public AdminController(MobileServiceAdmin mobileServiceAdmin, ResponseBean responseBean) {
		super();
		this.mobileServiceAdmin = mobileServiceAdmin;
		this.responseBean = responseBean;
		
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approveAdmin(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam String remarks) {
		responseBean.setRequestType("Completion of request by Admin");
		
		return mobileServiceAdmin.approveAdmin(regNumber, ip, email, remarks,responseBean);
		
	}

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		
		return mobileServiceAdmin.rejectAdmin(regNumber, ip, email, remarks, responseBean);
	}

	@RequestMapping(value = "/forwardToDa")
	public ResponseBean forwardToDa(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		
		return mobileServiceAdmin.forwardToDaAdmin(regNumber, ip, email, remarks, responseBean);
	}

	@RequestMapping(value = "/revert")
	public ResponseBean pull(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		
		return mobileServiceAdmin.pullAdmin(regNumber, ip, email, remarks, responseBean);
	}
}
