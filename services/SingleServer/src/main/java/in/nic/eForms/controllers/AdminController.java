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

import in.nic.eForms.models.AdminFormBean;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.services.SingleEmailAdminService;


@RequestMapping("/admin")
@Validated
@RestController
public class AdminController {

	private final SingleEmailAdminService singleEmailAdminService;
	private final ResponseBean responseBean;

	@Autowired
	public AdminController(SingleEmailAdminService singleEmailAdminService, ResponseBean responseBean) {
		super();
		this.singleEmailAdminService = singleEmailAdminService;
		this.responseBean = responseBean;
	}

	


	//@RequestMapping(value = "/approve")
	//public ResponseBean approve(@Valid @RequestBody AdminFormBean adminFormBean,@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
	//		@RequestParam("email") @NotEmpty String email, @RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) throws ParseException {
	//	responseBean.setRequestType("Completion of request by Admin");
	//	return singleEmailAdminService.approveAdmin(regNumber, ip, adminFormBean.getPo(), adminFormBean.getBo(), adminFormBean.getDomain(), email, adminFormBean.getFinalId(), adminFormBean.getPrimaryId(), remarks, responseBean);
	//}
	
	@RequestMapping(value = "/approve")
	public ResponseBean approve(@Valid @RequestBody AdminFormBean adminFormBean,@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) throws ParseException {
		responseBean.setRequestType("Completion of request by Admin");
		return singleEmailAdminService.approveAdmin(regNumber, ip, adminFormBean.getPo(), adminFormBean.getBo(), adminFormBean.getDomain(), email, adminFormBean.getFinalEmailId(), adminFormBean.getPrimaryId(), remarks, responseBean);
	}
	
	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		return singleEmailAdminService.rejectAdmin(regNumber, ip, email, remarks, responseBean);
	}

	@RequestMapping(value = "/forwardToDa")
	public ResponseBean forwardToDa(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		return singleEmailAdminService.forwardToDaAdmin(regNumber, ip, email, remarks, responseBean);
	}

	
	@RequestMapping(value = "/revert")
	public ResponseBean pull(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		return singleEmailAdminService.pullAdmin(regNumber, ip, email, remarks, responseBean);
	}

}
