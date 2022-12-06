package in.nic.eForms.controllers;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.services.EmailActCOService;


@Validated
@RequestMapping("/coordinator")
@RestController
public class CoordinatorController {
	private final EmailActCOService emailActCoService;
	private final ResponseBean responseBean;
	

	@Autowired
	public CoordinatorController(EmailActCOService emailActCoService, ResponseBean responseBean) {
		super();
		this.emailActCoService = emailActCoService;
		this.responseBean = responseBean;
		
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,String remarks) {
		return emailActCoService.approveCO(regNumber, ip, email, remarks, responseBean);
	}

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,  String remarks) {
		return emailActCoService.rejectCO(regNumber, ip, email, remarks, responseBean);
	}
	
	@RequestMapping(value = "/revert")
	public ResponseBean pull(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		return emailActCoService.pullCO(regNumber, ip, email, remarks, responseBean);
	}


}
