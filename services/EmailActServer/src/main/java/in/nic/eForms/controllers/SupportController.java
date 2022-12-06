package in.nic.eForms.controllers;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.services.EmailActSupportService;


@Validated
@RequestMapping("/support")
@RestController
public class SupportController {
	private final EmailActSupportService emailActSupportService;
	private final ResponseBean responseBean;
	

	@Autowired
	public SupportController(EmailActSupportService emailActSupportService, ResponseBean responseBean) {
		super();
		this.emailActSupportService = emailActSupportService;
		this.responseBean = responseBean;
		
	}
	
	@RequestMapping(value = "/forwardToCoordinator")
	public ResponseBean forwardToCoordinator(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email, String remarks,
			@RequestParam @NotEmpty String coordEmail) {
		responseBean.setRequestType("Support is forwarding the request to coordinator");
		return emailActSupportService.forwardToCoordinator(regNumber, ip, email, remarks, coordEmail, responseBean);
	}

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, String remarks) {
		return emailActSupportService.reject(regNumber, ip, email, remarks, responseBean);
	}

	@RequestMapping(value = "/forwardToDa")
	public ResponseBean forwardToDa(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks,
			@RequestParam @NotEmpty String daEmail) {
		return emailActSupportService.forwardToDa(regNumber, ip, email, remarks, daEmail, responseBean);
	}

	@RequestMapping(value = "/forwardToAdmin")
	public ResponseBean forwardToAdmin(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			 String remarks) {
		return emailActSupportService.forwardToAdmin(regNumber, ip, email, remarks, responseBean);
	}

	@RequestMapping(value = "/pull")
	public ResponseBean pull(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email) {
		return emailActSupportService.pull(regNumber, ip, email, responseBean);
	}
	

}
