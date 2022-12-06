package in.nic.ashwini.eForms.controllers;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.services.MobileServiceSupport;

@Validated
@RequestMapping("/support")
@RestController
public class SupportController {
	private final MobileServiceSupport mobileServiceSupport;
	private final ResponseBean responseBean;

	@Autowired
	public SupportController(MobileServiceSupport mobileServiceSupport, ResponseBean responseBean) {
		super();
		this.mobileServiceSupport = mobileServiceSupport;
		this.responseBean = responseBean;
		
	}

	@RequestMapping(value = "/forwardToCoordinator")
	public ResponseBean forwardToCoordinator(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks,
			@RequestParam @NotEmpty String coordEmail) {
		responseBean.setRequestType("Support is forwarding the request to coordinator");

		return mobileServiceSupport.forwardToCoordinator(regNumber, ip, email, remarks, coordEmail, responseBean);

	}

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {

		return mobileServiceSupport.reject(regNumber, ip, email, remarks, responseBean);
	}

	@RequestMapping(value = "/forwardToDa")
	public ResponseBean forwardToDa(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks,
			@RequestParam @NotEmpty String daEmail) {

		return mobileServiceSupport.forwardToDa(regNumber, ip, email, remarks, daEmail, responseBean);
	}

	@RequestMapping(value = "/forwardToAdmin")
	public ResponseBean forwardToAdmin(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {

		return mobileServiceSupport.forwardToAdmin(regNumber, ip, email, remarks, responseBean);
	}

	@RequestMapping(value = "/pull")
	public ResponseBean pull(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email) {

		return mobileServiceSupport.pull(regNumber, ip, email, responseBean);
	}

}
