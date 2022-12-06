package in.nic.eForms.controllers;

import java.text.ParseException;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.services.EmailDeActAdminService;


@RequestMapping("/admin")
@Validated
@RestController
public class AdminController {

	private final EmailDeActAdminService emailDeActAdminService;
	private final ResponseBean responseBean;

	@Autowired
	public AdminController(EmailDeActAdminService emailDeActAdminService, ResponseBean responseBean) {
		super();
		this.emailDeActAdminService = emailDeActAdminService;
		this.responseBean = responseBean;
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam("remarks") @NotEmpty String remarks) throws ParseException {
		responseBean.setRequestType("Completion of request by Admin");
	return emailDeActAdminService.approveAdmin(regNumber, ip, email, remarks, responseBean);
	}
	
	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam("remarks") @NotEmpty String remarks) {
		return emailDeActAdminService.rejectAdmin(regNumber, ip, email, remarks, responseBean);
	}

	@RequestMapping(value = "/forwardToDa")
	public ResponseBean forwardToDa(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email, @RequestParam("remarks") @NotEmpty String remarks) {
		return emailDeActAdminService.forwardToDaAdmin(regNumber, ip, email, remarks, responseBean);
	}

	
	@RequestMapping(value = "/revert")
	public ResponseBean pull(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam("remarks") @NotEmpty String remarks) {
		return emailDeActAdminService.pullAdmin(regNumber, ip, email, remarks, responseBean);
	}

}
