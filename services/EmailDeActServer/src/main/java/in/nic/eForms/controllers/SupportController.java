package in.nic.eForms.controllers;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.services.EmailDeActSupportService;


@Validated
@RequestMapping("/support")
@RestController
public class SupportController {
	private final EmailDeActSupportService emailDeActSupportService;
	private final ResponseBean responseBean;
	

	@Autowired
	public SupportController(EmailDeActSupportService emailDeActSupportService, ResponseBean responseBean) {
		super();
		this.emailDeActSupportService = emailDeActSupportService;
		this.responseBean = responseBean;
		
	}
	
	@RequestMapping(value = "/forwardToCoordinator")
	public ResponseBean forwardToCoordinator(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email, String remarks,
			@RequestParam @NotEmpty String coordEmail) {
		responseBean.setRequestType("Support is forwarding the request to coordinator");
		return emailDeActSupportService.forwardToCoordinator(regNumber, ip, email, remarks, coordEmail, responseBean);
	}

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, String remarks) {
		return emailDeActSupportService.reject(regNumber, ip, email, remarks, responseBean);
	}

	@RequestMapping(value = "/forwardToDa")
	public ResponseBean forwardToDa(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email, String remarks,
			@RequestParam @NotEmpty String daEmail) {
		return emailDeActSupportService.forwardToDa(regNumber, ip, email, remarks, daEmail, responseBean);
	}

	@RequestMapping(value = "/forwardToAdmin")
	public ResponseBean forwardToAdmin(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			 String remarks) {
		return emailDeActSupportService.forwardToAdmin(regNumber, ip, email, remarks, responseBean);
	}

	@RequestMapping(value = "/pull")
	public ResponseBean pull(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email) {
		return emailDeActSupportService.pull(regNumber, ip, email, responseBean);
	}
	

}
